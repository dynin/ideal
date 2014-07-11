/// Start of ideal JavaScript runtime.

function start() {
  process_world_schema(WORLD_SCHEMA_JSON);
  process_fluid_code(WORLD_FLUID_CODE);
  update_world_state(WORLD_STATE_JSON);
  init_view();

  set_status_online();
  display_show_world();
  schedule_sync_request();
}

function display_show_world() {
  set_main_view(show_world());
  enable_pull(display_show_world);
}

function show_world() {
  var rows = new Array();

  for (var i = 0; i < data_types.length; ++i) {
    var data_type = data_types[i];

    var columns = new Array();
    columns.push(new bold(to_display(data_type.name)));
    var values = of_type(data_type);
    if (values.length == 0) {
      columns.push(EMPTY_WIDGET);
      columns.push(EMPTY_WIDGET);
    } else {
      var ref = new simple_ref(data_type, null);
      columns.push(show_data_values(ref, values, false));
      columns.push(save_button('Edit', bind1st_ref(edit_value_page, ref)));
      if (data_type.lookup_method(PUBLISH)) {
	columns.push(save_button('Publish', bind1st_ref(publish_page, ref)));
      }
    }
    columns.push(new button('Add', bind1st(add_value_page, data_type)));
    rows.push(new hbox(columns));
  }

  return new vbox(rows);
}

function render_edit_view(value, action_name, action) {
  assert (value instanceof data_value);
  var fields = value.get_fields();
  var rows = new Array();
  for (var i = 0; i < fields.length; ++i) {
    var columns = new Array();
    columns.push(new bold(to_display(fields[i].name)));
    columns.push(make_view_of(fields[i]));
    rows.push(new hbox(columns));
  }
  rows.push(save_button(action_name + ' ' + to_display(value.type.name), action));
  rows.push(make_back_button());

  set_main_view(new vbox(rows));
  disable_pull();
}

function make_back_button() {
  return new button('\u00AB Back', display_show_world);
}

function add_value_page(dt) {
  var value = dt.new_value();
  render_edit_view(value, 'Add', function() {
      add_data_value(value);
      display_show_world();
    });
}

function edit_value_page(dv) {
  render_edit_view(dv, 'Update', display_show_world);
}

function publish_page(dv) {
  var publisher = dv.type.lookup_method(PUBLISH);
  assert (publisher);
  var published_html = publisher(dv);

  var rows = new Array();
  rows.push(new html_div(published_html));
  rows.push(make_back_button());

  set_main_view(new div_vbox(rows));

  // This way, data value may be updated by a pull request
  var data_id = dv.get_id();
  enable_pull(function() { publish_page(by_id(data_id)); });
}

var PUBLISH = 'publish';

/// Sync protocol implementation

var SYNC_INTERVAL_MS = 2000;
var TIMEOUT_INTERVAL_MS = 2000;
var PULL = 'PULL';
var PUSH = 'PUSH';
var is_request_pending = false;
var is_request_in_progress = false;
var is_pull_enabled;
var view_refresh_function;
var should_push = false;

function schedule_sync_request() {
  if (!is_request_pending && !is_request_in_progress) {
    setTimeout(start_sync_request, SYNC_INTERVAL_MS);
    is_request_pending = true;
  }
}

function enable_pull(the_view_refresh_function) {
  is_pull_enabled = true;
  view_refresh_function = the_view_refresh_function;
}

function disable_pull() {
  is_pull_enabled = false;
  view_refresh_function = null;
}

function client_state_changed() {
  should_push = true;
}

function start_sync_request() {
  is_request_pending = false;
  if (should_push) {
    start_push_request();
  } else if (is_pull_enabled) {
    start_pull_request();
  } else {
    schedule_sync_request();
  }
}

function start_pull_request() {
  is_request_in_progress = true;
  var payload = { 'version_id': server_version_id, 'source_version': source_version };
  start_request(PULL, JSON.stringify(payload), pull_callback, pull_error_callback);
}

function pull_callback(response) {
  is_request_in_progress = false;
  set_status_online();

  if (is_pull_enabled) {
    if (response && response != 'Ok') {
      response_json = JSON.parse(response);
      assert (response_json != null);

      if (response_json.world_schema) {
        set_app_version(response_json.source_version);
        process_world_schema(response_json.world_schema);
        process_fluid_code(eval("({" + response_json.fluid_code + "})"));
      }
      update_world_state(response_json);

      view_refresh_function();
    }
  }

  schedule_sync_request();
}

function pull_error_callback() {
  is_request_in_progress = false;
  set_status_offline();
  schedule_sync_request();
}

function start_push_request() {
  is_request_in_progress = true;
  var version_id = generate_version_id();
  var payload = { 'version_id': version_id, 'data': values_state };
  server_version_id = version_id;
  should_push = false;
  start_request(PUSH, JSON.stringify(payload), push_callback, push_error_callback);
}

function push_callback(response) {
  is_request_in_progress = false;
  set_status_online();
  schedule_sync_request();
}

function push_error_callback() {
  is_request_in_progress = false;
  set_status_offline();
  should_push = true;
  schedule_sync_request();
}

/// Type handling

var data_types;
var types_by_name;
var world_version;
var all_methods;

var values_state;
var data_values;
var values_by_id;
var server_version_id;
var source_version;
var next_id = 0;
var instance_id = 0;

var DATA_ID = 'data_id';
var DATA_TYPE = 'data_type';
var NAME = 'name';

var METHOD_PREFIX = 'method_';

function data_type(data_type_json) {
  assert (data_type_json.name != null);
  assert (data_type_json.fields != null);

  this.name = data_type_json.name;
  this.fields = data_type_json.fields;
}

data_type.prototype.default_value = function() {
  return null;
}

data_type.prototype.new_value = function() {
  var result = { 'data_type': this.name };

  var type_fields = this.fields;
  for (var i = 0; i < type_fields.length; ++i) {
    if (is_list_type(type_fields[i][1])) {
      result[type_fields[i][0]] = [];
    }
  }

  return new data_value(this, result);
}

data_type.prototype.lookup_method = function(name) {
  var method_name = METHOD_PREFIX + this.name + '_' + name;
  return all_methods[method_name];
}

data_type.prototype.make_view = function(model) {
  return show_data_values(model, of_type(this), true);
}

function enum_type(enum_type_json) {
  assert (enum_type_json.name != null);
  assert (enum_type_json.values != null);

  this.name = enum_type_json.name;
  this.values = enum_type_json.values;
}

enum_type.prototype.default_value = function() {
  return this.values[0];
}

enum_type.prototype.make_view = function(model) {
  return new select_input(model, this.values, to_display, identity);
}

function data_value(dt, state) {
  this.type = dt;
  this.state = state;
}

data_value.prototype.get_fields = function() {
  var type_fields = this.type.fields;
  var fields = new Array();
  for (var i = 0; i < type_fields.length; ++i) {
    fields.push(new data_field(this, type_fields[i]));
  }
  return fields;
}

data_value.prototype.get_field = function(name) {
  var type_fields = this.type.fields;
  for (var i = 0; i < type_fields.length; ++i) {
    if (name == type_fields[i][0]) {
      var type = get_type_by_name(type_fields[i][1]);
      return from_state(this.state[name], type);
    }
  }
  throw ("Couldn't find " + name);
}

data_value.prototype.get_id = function() {
  var id = this.state[DATA_ID];
  assert (id != null);
  return id;
}

data_value.prototype.name = function() {
  var method = this.type.lookup_method(NAME);
  if (method) {
    return method(this);
  }

  if (this.state[NAME]) {
    return this.state[NAME];
  } else {
    return this.get_id();
  }
}

function simple_ref(type, value) {
  this.type = type;
  this.value = value;
}

simple_ref.prototype.get_type = function() {
  return this.type;
}

simple_ref.prototype.get = function() {
  return this.value;
}

simple_ref.prototype.set = function(new_value) {
  this.value = new_value;
}

function make_view_of(ref) {
  return ref.get_type().make_view(ref);
}

function from_state(value, type) {
  if (value) {
    if (type instanceof data_type) {
      return by_id(value);
    } else if (type instanceof list_type) {
      var result = new Array();
      for (var i = 0; i < value.length; ++i) {
        result.push(from_state(value[i], type.element_type));
      }
      return { 'state' : value, 'elements' : result };
    } else {
      return value;
    }
  } else {
    return type.default_value();
  }
}

function to_state(value) {
  if (value instanceof data_value) {
    return value.get_id();
  } else {
    return value;
  }
}

function data_field(dv, field_info) {
  this.value = dv;
  this.name = field_info[0];
  this.type_name = field_info[1];
}

data_field.prototype.get_type = function() {
  if (!this.type) {
    this.type = get_type_by_name(this.type_name);
  }
  return this.type;
}

data_field.prototype.get = function() {
  return from_state(this.value.state[this.name], this.get_type());
}

data_field.prototype.set = function(new_value) {
  this.value.state[this.name] = to_state(new_value);
  client_state_changed();
}

var STRING = 'string';

function string_type() {
}

string_type.prototype.default_value = function() {
  return '';
}

string_type.prototype.make_view = function(model) {
  return new text_input(model);
}

var LIST_PREFIX = 'list/'
var NUM_ROWS = 15;

function list_type(element_type) {
  this.element_type = element_type;
}

list_type.prototype.default_value = function() {
  return new Array();
}

list_type.prototype.make_view = function(model) {
  var rows = new Array();
  for (var i = 0; i < NUM_ROWS; ++i) {
    rows.push(make_view_of(new element_reference(model.get().state, i, this.element_type)));
  }
  return new vbox(rows);
}

function element_reference(list_value, index, element_type) {
  this.list_value = list_value;
  this.index = index;
  this.element_type = element_type;
}

element_reference.prototype.get_type = function() {
  return this.element_type;
}

element_reference.prototype.get = function() {
  if (this.index < this.list_value.length) {
    return from_state(this.list_value[this.index], this.element_type);
  } else {
    return this.element_type.default_value();
  }
}

element_reference.prototype.set = function(new_value) {
  var elements = this.list_value;
  if (new_value != null || this.index < elements.length) {
    while (elements.length <= this.index) {
      elements.push(null);
    }
    elements[this.index] = to_state(new_value);
    client_state_changed();
  }
}

function process_world_schema(world_schema_json) {
  data_types = new Array();
  types_by_name = { };

  types_by_name[STRING] = new string_type();

  var data_types_json = world_schema_json.data_types;
  for (var i = 0; i < data_types_json.length; ++i) {
    var dt = new data_type(data_types_json[i]);
    data_types.push(dt);
    types_by_name[dt.name] = dt;
  }

  var enum_types_json = world_schema_json.enum_types;
  for (var i = 0; i < enum_types_json.length; ++i) {
    var et = new enum_type(enum_types_json[i]);
    types_by_name[et.name] = et;
  }

  world_version = world_schema_json.version;
  assert (world_version != null);
}

function process_fluid_code(fluid_code_object) {
  assert (fluid_code_object != null);
  all_methods = fluid_code_object;
}

function is_list_type(type_name) {
  return type_name.slice(0, LIST_PREFIX.length) == LIST_PREFIX;
}

function get_element_type(type_name) {
  return type_name.slice(LIST_PREFIX.length);
}

function get_type_by_name(name) {
  var result = types_by_name[name];

  if (result == null) {
    if (is_list_type(name)) {
      var element_type = get_type_by_name(get_element_type(name));
      result = new list_type(element_type);
      types_by_name[name] = result;
    }
    return result;
  }

  if (result != null) {
    return result;
  } else {
    throw 'Unknown type ' + name;
  }
}

function generate_version_id() {
  return 'client/' + world_version + '/' + (new Date().getTime()) + '/' + (instance_id++);
}

function update_world_state(world_state_json) {
  data_values = new Array();
  values_by_id = { };

  values_state = world_state_json.data;
  assert (values_state != null);

  for (var i = 0; i < values_state.length; ++i) {
    var state = values_state[i];
    var dt = types_by_name[state[DATA_TYPE]];
    assert (dt instanceof data_type);
    register_data_value(new data_value(dt, state));
  }

  server_version_id = world_state_json.version_id;
  assert (server_version_id != null);

  source_version = world_state_json.source_version;
  assert (source_version != null);

  should_push = false;
}

function register_data_value(dv) {
  assert (dv.state[DATA_ID] != null);
  data_values.push(dv);
  values_by_id[dv.get_id()] = dv;
}

function get_next_id() {
  var string_id;

  do {
    string_id = 'id:' + (next_id++);
  } while (values_by_id[string_id]);

  return string_id;
}

function add_data_value(dv) {
  assert (!dv.state[DATA_ID]);
  dv.state[DATA_ID] = get_next_id();

  register_data_value(dv);
  values_state.push(dv.state);

  client_state_changed();
}

function show_data_values(model, values, show_null) {
  var options = new Array();
  if (show_null) {
    options.push(null);
  }
  options = options.concat(values);
  options.sort(data_value_sorter);
  return new select_input(model, options, data_value_displayer, data_value_identifier);
}

function of_type(dt) {
  return data_values.filter(function(dv) { return dv.type == dt; });
}

function by_id(id) {
  result = values_by_id[id];
  if (result == null) {
    throw "Can't find data for id " + id;
  }
  return result;
}

function data_value_displayer(dv) {
  if (dv == null) {
    return '';
  } else {
    return dv.name();
  }
}

function data_value_identifier(dv) {
  if (dv == null) {
    return 'null';
  } else {
    return dv.get_id();
  }
}

function data_value_sorter(dv1, dv2) {
  var s1 = data_value_displayer(dv1);
  var s2 = data_value_displayer(dv2);
  return s1.localeCompare(s2);
}

/// Widget implementations

var main_element;
var status_element;
var app_version_element;
var main_view_widget;

function init_view() {
  main_element = document.getElementById('main');
  assert (main_element != null);
  status_element = document.getElementById('status');
  assert (status_element != null);
  app_version_element = document.getElementById('app_version');
  assert (app_version_element != null);
}

function set_main_view(w) {
  main_view_widget = w;

  remove_children(main_element);
  main_element.appendChild(w.render());
}

function set_status(message, color) {
  remove_children(status_element);
  status_element.appendChild(document.createTextNode(message));
  status_element.setAttribute('class', color);
}

function set_status_online() {
  set_status('Online', 'green');
}

function set_status_offline() {
  set_status('Offline', 'yellow');
}

function set_app_version(app_version) {
  remove_children(app_version_element);
  app_version_element.appendChild(document.createTextNode(app_version));
}

function remove_children(e) {
  while (e.hasChildNodes()) {
    e.removeChild(e.firstChild);
  }
}

function update_main_model() {
  assert (main_view_widget != null);
  update_model(main_view_widget);
}

function update_model(widget) {
  if (widget.update) {
    widget.update();
  }
}

function label(content) {
  this.content = content;
}

label.prototype.render = function() {
  return document.createTextNode(this.content);
}

var EMPTY_WIDGET = new label('');

function bold(content) {
  this.content = content;
}

bold.prototype.render = function() {
  var tag = document.createElement('b');
  tag.appendChild(document.createTextNode(this.content));
  return tag;
}

function html_div(content) {
  this.content = content;
}

html_div.prototype.render = function() {
  var element = document.createElement('div');
  element.innerHTML = this.content;
  return element;
}

function vbox(rows) {
  this.rows = rows;
}

vbox.prototype.render = function() {
  var table = document.createElement('table');
  for (var i = 0; i < this.rows.length; ++i) {
    var row_tag = document.createElement('tr');
    var row = this.rows[i];
    if (row instanceof hbox) {
      row_tag.appendChild(row.render());
    } else {
      var cell = document.createElement('td');
      cell.appendChild(row.render());
      row_tag.appendChild(cell);
    }
    table.appendChild(row_tag);
  }
  return table;
}

vbox.prototype.update = function() {
  for (var i = 0; i < this.rows.length; ++i) {
    update_model(this.rows[i]);
  }
}

function div_vbox(rows) {
  this.rows = rows;
}

div_vbox.prototype.render = function() {
  var nodes = document.createDocumentFragment();
  for (var i = 0; i < this.rows.length; ++i) {
    var div = document.createElement('div');
    div.appendChild(this.rows[i].render());
    nodes.appendChild(div);
  }
  return nodes;
}

div_vbox.prototype.update = function() {
  for (var i = 0; i < this.rows.length; ++i) {
    update_model(this.rows[i]);
  }
}

function hbox(columns) {
  this.columns = columns;
}

hbox.prototype.render = function() {
  var nodes = document.createDocumentFragment();
  for (var i = 0; i < this.columns.length; ++i) {
    var cell = document.createElement('td');
    cell.appendChild(this.columns[i].render());
    nodes.appendChild(cell);
  }
  return nodes;
}

hbox.prototype.update = function() {
  for (var i = 0; i < this.columns.length; ++i) {
    update_model(this.columns[i]);
  }
}

function button(name, action) {
  this.name = name;
  this.action = action;
}

function save_button(name, action) {
  return new button(name, function() { update_main_model(); action(); });
}

button.prototype.render = function() {
  var input = document.createElement('input');
  input.setAttribute('type', 'submit');
  input.setAttribute('value', this.name);
  input.onclick = this.action;
  return input;
}

function text_input(model) {
  this.model = model;
}

text_input.prototype.render = function() {
  var input = document.createElement('input');
  input.setAttribute('type', 'text');
  input.setAttribute('value', this.model.get());
  this.html_input = input;
  return input;
}

text_input.prototype.update = function() {
  if (this.html_input && this.html_input.value) {
    this.model.set(this.html_input.value);
  }
}

function select_input(model, options, displayer, identifier) {
  this.model = model;
  this.options = options;
  this.displayer = displayer;
  this.identifier = identifier;
}

select_input.prototype.render = function() {
  var selected_option = this.model.get();

  var select = document.createElement('select');
  for (var i = 0; i < this.options.length; ++i) {
    var option = this.options[i];
    var option_tag = document.createElement('option');
    option_tag.setAttribute('value', this.identifier(option));
    if (option == selected_option) {
      option_tag.setAttribute('selected', 'selected');
    }
    option_tag.appendChild(document.createTextNode(this.displayer(option)));
    select.appendChild(option_tag);
  }
  this.html_select = select;
  return select;
}

select_input.prototype.update = function() {
  if (this.html_select && this.html_select.value) {
    var selected = this.html_select.value;

    for (var i = 0; i < this.options.length; ++i) {
      var option = this.options[i];
      if (this.identifier(option) == selected) {
	this.model.set(option);
	return;
      }
    }
  }
}

/// XMLHttpRequest handling

function start_request(request_type, payload, callback, error_callback) {
  log('Requesting ' + request_type + digest(payload));
  var request = new XMLHttpRequest();

  request.open('POST', SYNC_REQUEST_URI, true);
  request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded'); 

  var active = true;

  request.onreadystatechange = function() {
    if (request.readyState != 4) {
      return;
    }

    if (!active) {
      return;
    }

    active = false;

    try {
      if (request.status == 200 && request.responseText) {
        log('Request ' + request_type + ' succeeded' + digest(request.responseText));
        if (callback) {
          try {
            callback(request.responseText);
          } catch (e) {
            log('Callback failed: ' + e);
          }
        }
      } else {
        log('Request ' + request_type + ' failed (status: ' + request.status + ').');
        if (error_callback) {
          error_callback();
        }
      }
    } catch (e) {
      log('Request ' + request_type + ' failed (exception).');
      if (error_callback) {
        error_callback();
      }
    }
  };

  var content = 'request_type=' + encode_form_value(request_type);
  if (payload) {
    content += '&payload=' + encode_form_value(payload);
  }
  request.send(content);

  setTimeout(function() {
    if (!active) {
      return;
    }
    active = false;
    request.abort();
    log('Request ' + request_type + ' timed out.');
    if (error_callback) {
      error_callback();
    }
  }, TIMEOUT_INTERVAL_MS);
}

function digest(message) {
  if (message) {
    var s = ': ';
    if (message.length > 100) {
      s = s + message.substring(0, 100) + '...';
    } else {
      s = s + message;
    }
    return s;
  } else {
    return '';
  }
}

function log(message) {
  console.log(message);
}

function encode_form_value(str) {
  function to_hex(c) { return '0123456789ABCDEF'.charAt(c); };

  result = '';
  for (var i = 0; i < str.length; ++i) {
    var c = str.charCodeAt(i);
    if ((c >= 48 && c <= 57) ||   // 0-9
        (c >= 65 && c <= 90) ||   // A-Z
        (c >= 97 && c <= 122)) {  // a-z
      result += str.charAt(i);
    } else {
      result += '%' + to_hex(parseInt(c / 16)) + to_hex(c % 16);
    }
  }

  return result;
}

/// Utility functions

function assert(expression) {
  if (!expression) {
    throw ('There is a bug.');
  }
}

function bind1st(fn, arg_value) {
  return function() { fn(arg_value); }
}

function bind1st_ref(fn, arg_ref) {
  return function() { fn(arg_ref.get()); }
}

function identity(obj) {
  return obj;
}

function to_display(s) {
  if (s == '') {
    return s;
  }

  var result = s.charAt(0).toUpperCase();

  for (var i = 1; i < s.length; ++i) {
    var c = s.charAt(i);
    if (c == '_') {
      c = ' ';
    } else {
      c = c.toLowerCase();
    }
    result += c;
  }

  return result;
}

function escape_html(s) {
  return s.replace(/&/g, '&amp;')
          .replace(/>/g, '&gt;')
          .replace(/</g, '&lt;')
          .replace(/'/g, '&apos;')
          .replace(/"/g, '&quot;');
}

/// Call start() on load

var body_element = document.getElementsByTagName('body')[0];
assert (body_element != null);
body_element.onload = start;

/// End of ideal JavaScript runtime.

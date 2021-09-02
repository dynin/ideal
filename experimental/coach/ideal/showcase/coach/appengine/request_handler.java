/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.tools.reflect_util;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.forms.*;
import ideal.showcase.coach.forms.select_input.option_displayer;
import ideal.showcase.coach.webforms.*;
import ideal.showcase.coach.marshallers.*;
import ideal.showcase.coach.common.*;

/**
 * The framework for an ideal framework forms-based application.
 *
 * TODO:
 * Add CREATED and MODIFIED
 */
public class request_handler extends base_handler {

  private static final procedure1_data_type ADD_VALUE_PAGE =
      new procedure1_data_type("add_value_page");
  private static final procedure1_data_type ADD_VALUE_ACTION =
      new procedure1_data_type("add_value_action");
  private static final procedure1_data_type EDIT_VALUE_PAGE =
      new procedure1_data_type("edit_value_page");
  private static final procedure1_data_type PUBLISH_ACTION =
      new procedure1_data_type("publish_action");

  private static final procedure1_data_value EDIT_VALUE_ACTION =
      new procedure1_data_value("edit_value_action");

  private static final base_procedure0 SHOW_WORLD = new base_procedure0("show_world");
  private static final base_procedure0 EXPORT_PAGE = new base_procedure0("export_page");
  private static final base_procedure0 IMPORT_PAGE = new base_procedure0("import_page");
  private static final base_procedure0 IMPORT_ACTION = new base_procedure0("import_action");
  private static final base_procedure0 VIEW_SOURCE_PAGE = new base_procedure0("view_source_page");
  private static final base_procedure0 EDIT_SOURCE_PAGE = new base_procedure0("edit_source_page");
  private static final base_procedure0 EDIT_SOURCE_ACTION =
      new base_procedure0("edit_source_action");
  private static final base_procedure0 RESET_SOURCE_ACTION =
      new base_procedure0("reset_source_action");
  private static final base_procedure0 JAVASCRIPT_PAGE = new base_procedure0("javascript_page");
  private static final base_procedure0 SYNC = new base_procedure0("sync");
  private static final base_procedure0 MSYNC = new base_procedure0("msync");

  public static final procedure0arg DEFAULT_PAGE = SHOW_WORLD;

  private static final procedure_id[] REQUEST_TYPES = {
      SHOW_WORLD, ADD_VALUE_PAGE, ADD_VALUE_ACTION, EDIT_VALUE_PAGE, EDIT_VALUE_ACTION,
      PUBLISH_ACTION, EXPORT_PAGE, IMPORT_PAGE, IMPORT_ACTION, VIEW_SOURCE_PAGE,
      EDIT_SOURCE_PAGE, EDIT_SOURCE_ACTION, RESET_SOURCE_ACTION, JAVASCRIPT_PAGE, SYNC, MSYNC
  };

  private static dictionary<string, procedure_id> handlers =
      new hash_dictionary<string, procedure_id>();

  static {
    for (procedure_id id : REQUEST_TYPES) {
      id.init(request_handler.class);
      handlers.put(id.to_string(), id);
    }
  }

  private static final int NUM_ROWS = 15;

  public request_handler(user_state state, fluid_context request) {
    super(state, request);
  }

  protected dictionary<string, procedure_id> get_handlers() {
    return handlers;
  }

  private void update_model(widget the_widget) {
    new model_updater(get_world(), request).update_model(the_widget);
  }

  private @Nullable data_value get_current_value(data_type dt) {
    dictionary<data_type, reference_wrapper<any_composite_value>> all_data_selectors =
        new hash_dictionary<data_type, reference_wrapper<any_composite_value>>();
    update_model(do_show_world(all_data_selectors));

    reference_wrapper<any_composite_value> the_reference = all_data_selectors.get(dt);
    if (the_reference != null) {
      return (data_value) the_reference.get();
    } else {
      return null;
    }
  }

  @Override
  public text_content widget_to_html(widget content) {
    return to_html(content, false, null);
  }

  @Override
  public text_content show_error_message(error_message message) {
    return to_html(show_world(), false, message);
  }

  private text_content to_html(widget content, boolean javascript_view,
      @Nullable error_message message) {
    view_renderer renderer = new view_renderer(request);

    StringBuilder sb = new StringBuilder();
    sb.append("<html>\n<head>\n<title>");
    sb.append(view_renderer.escape_html(state.app_name()));
    if (javascript_view) {
      sb.append(" (JavaScript)");
    }
    sb.append("</title>\n");
    sb.append("</head>\n");
    sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/ideal.css\" />\n");
    sb.append("<body>\n");
    sb.append("<div>Hello, " + view_renderer.escape_html(request.get_user()) + "!</div>\n");
    sb.append("<hr>\n");

    if (javascript_view) {
      sb.append("<div id='main'>...</div>\n");
      sb.append(renderer.render(content));
    } else {
      if (message != null) {
        sb.append("<div class='error-message'>\n");
        sb.append(view_renderer.escape_html(message.content));
        sb.append("\n</div>\n");
      }
      form the_form = new form(content, request.base_uri());
      sb.append(renderer.render_form_start(the_form));
      sb.append(renderer.render_hidden_input(marshaller.ORIGINAL_VERSION_ID,
          get_world().get_version_id()));
      sb.append(renderer.render(the_form.content));
      sb.append(renderer.render_form_end());
    }

    sb.append("<hr>\n");
    sb.append("<em>");
    sb.append(renderer.render(new simple_link(state.app_name(), request.base_uri())));
    sb.append(" v<span id='app_version'>");
    sb.append(view_renderer.escape_html(get_app_version()));
    sb.append("</span>");
    if (request.admin_mode) {
      sb.append(" (admin mode)");
    }
    sb.append("</em>");

    if (javascript_view) {
      sb.append(" <span id='status' class='green'>Loading</span>\n");
    }

    return new text_content(resource_util.TEXT_HTML, new base_string(sb.toString()));
  }

  public widget show_world() {
    return do_show_world(null);
  }

  private widget do_show_world(
      @Nullable dictionary<data_type, reference_wrapper<any_composite_value>> value_selectors) {
    list<widget> rows = new base_list<widget>();

    readonly_list<data_type> all_data_types = get_schema().all_data_types();
    for (int i = 0; i < all_data_types.size(); ++i) {
      data_type dt = all_data_types.get(i);
      list<widget> columns = new base_list<widget>();

      columns.append(new bold(to_display(dt.short_name()) + "s:"));

      readonly_list<data_value> values = get_world().of_type(dt);
      if (values.is_empty()) {
        columns.append(widget.EMPTY_WIDGET);
        columns.append(widget.EMPTY_WIDGET);
      } else {
        type_id value_type = dt.value_type();
        reference_wrapper<any_composite_value> value_selector =
            new simple_reference<any_composite_value>(value_type,
                get_schema().get_mutable_reference(value_type), null);
        if (value_selectors != null) {
          value_selectors.put(dt, value_selector);
        }
        columns.append(new select_input(value_selector, values, new data_displayer(), true));

        columns.append(new button("Edit", EDIT_VALUE_PAGE.bind(dt)));
        if (dt.supports_publish()) {
          columns.append(new button("Publish", PUBLISH_ACTION.bind(dt)));
        }
      }
      columns.append(new button("Add", ADD_VALUE_PAGE.bind(dt)));
      rows.append(new hbox(columns));
    }

    rows.append(new link("Export data", EXPORT_PAGE));
    if (request.admin_mode) {
      rows.append(new link("Import data", IMPORT_PAGE));
      rows.append(new link("View source", VIEW_SOURCE_PAGE));
      rows.append(new link("Edit source", EDIT_SOURCE_PAGE));
      rows.append(new link("JavaScript view", JAVASCRIPT_PAGE));
    }
    rows.append(new simple_link(new base_string("Logout"), request.logout_url));

    return new vbox(rows);
  }

  private boolean update_collision() {
    string current_version_id = get_world().get_version_id();
    @Nullable string request_version_id = request.get(marshaller.ORIGINAL_VERSION_ID);

    return !utilities.eq(current_version_id, request_version_id);
  }

  private string marshal_world() {
    return get_marshaller().marshal_state(get_world()).stringify();
  }

  public text_content view_source_page() {
    return new text_content(resource_util.TEXT_PLAIN, get_schema().source_content());
  }

  public text_content export_page() {
    checkpoint_world_state(get_world());

    return new text_content(resource_util.TEXT_PLAIN, marshal_world());
  }

  private widget import_view(reference_wrapper<string> text) {
    list<widget> rows = new base_list<widget>();

    rows.append(new textarea_input(text));
    rows.append(new button("Import (overwrites data!)", IMPORT_ACTION));

    return new vbox(rows);
  }

  private reference_wrapper<string> make_reference(@Nullable string value) {
    type_id string_type = get_schema().immutable_string_type();
    return new simple_reference<string>(string_type,
        get_schema().get_mutable_reference(string_type),
        value != null ? get_schema().new_string(value) : null);
  }

  public widget import_page() {
    reference_wrapper<string> text = make_reference(marshal_world());
    return import_view(text);
  }

  public widget edit_source_page() {
    reference_wrapper<string> source = make_reference(get_schema().source_content());
    return render_source(source, null);
  }

  private widget render_source(reference_wrapper<string> source, @Nullable widget top) {
    list<widget> rows = new base_list<widget>();

    if (top != null) {
      rows.append(top);
    }
    rows.append(new textarea_input(source));
    rows.append(new button("Update code (can break things!)", EDIT_SOURCE_ACTION));
    rows.append(new button("Reset code", RESET_SOURCE_ACTION));

    return new vbox(rows);
  }

  public widget edit_source_action() {
    reference_wrapper<string> source_ref = make_reference(null);
    update_model(render_source(source_ref, null));

    if (source_ref.get() == null) {
      return null;
    }
    string new_source = source_ref.get().unwrap();

    translation_result edit_result = translate_source(new_source);
    if (edit_result.is_success()) {
      return null;
    } else {
      reference_wrapper<string> source = make_reference(new_source);
      text_fragment error_messages = edit_result.get_error_messages();
      return render_source(source, new div("messages", new html_text(error_messages)));
    }
  }

  public void reset_source_action() {
    state.reset_source();
  }

  private widget render_edit_value_page(data_value dv, @Nullable String button_name,
      @Nullable procedure0arg button_action) {
    list<widget> rows = new base_list<widget>();

    readonly_list<field_reference> all_fields = dv.get_fields();
    for (int i = 0; i < all_fields.size(); ++i) {
      rows.append(render_field_row(all_fields.get(i)));
    }

    if (button_name != null && button_action != null) {
      String submit_name = button_name + " " + to_display(dv.get_type().short_name());
      rows.append(new button(submit_name, button_action));
    }

    return new vbox(rows);
  }

  public widget add_value_page(data_type type) {
    return render_edit_value_page(type.new_value(get_world()), "Add", ADD_VALUE_ACTION.bind(type));
  }

  public Object edit_value_page(data_type dt) {
    data_value dv = get_current_value(dt);
    if (dv == null) {
      return new error_message("Not found item selection for " + dt.short_name());
    }
    return render_edit_value_page(dv, "Update", EDIT_VALUE_ACTION.bind(dv));
  }

  public widget render_field_row(field_reference field) {
    list<widget> columns = new base_list<widget>();
    columns.append(new bold(to_display(field.short_name())));
    columns.append(render_input(field));
    return new hbox(columns);
  }

  public widget render_input(reference_wrapper field) {
    type_id value_type = field.value_type_bound();

    if (value_type == get_schema().immutable_string_type()) {
      return render_string_input((reference_wrapper<string>) field);
    } else if (get_schema().is_enum_type(value_type)) {
      return render_enum_input((reference_wrapper<enum_value>) field);
    } else if (get_schema().is_data_type(value_type)) {
      return render_data_input((reference_wrapper<data_value>) field);
    } else if (get_schema().is_list_type(value_type)) {
      return render_list_input((reference_wrapper<list<value_wrapper>>) field);
    } else {
      throw new RuntimeException("Uncrecognized type " + value_type);
    }
  }

  public widget render_string_input(reference_wrapper<string> field) {
    return new text_input(field);
  }

  public widget render_enum_input(reference_wrapper<enum_value> field) {
    enum_type the_type = get_schema().get_enum_type(field.value_type_bound());
    return new select_input(field, the_type.get_values(), new enum_displayer(), false);
  }

  public static class enum_displayer implements option_displayer<enum_value> {
    @Override
    public String display(enum_value value) {
      return to_display(value.short_name());
    }
  }

  public widget render_data_input(reference_wrapper<data_value> field) {
    data_type the_type = get_schema().get_data_type(field.value_type_bound());
    list<data_value> values = get_world().of_type(the_type);
    values.append(null);
    return new select_input(field, values, new data_displayer(), true);
  }

  public static class data_displayer implements option_displayer<data_value> {
    @Override
    public String display(@Nullable data_value value) {
      if (value == null) {
        return "";
      } else {
        return value.get_name();
      }
    }
  }

  public widget render_list_input(reference_wrapper<list<value_wrapper>> field) {
    list<widget> rows = new base_list<widget>();

    for (int i = 0; i < NUM_ROWS; ++i) {
      rows.append(render_input(new element_reference(field.get(), i)));
    }

    return new vbox(rows);
  }

  public void add_value_action(data_type type) {
    datastore_state world = get_world();
    data_value new_value = type.new_value(world);
    widget view = render_edit_value_page(new_value, null, null);
    update_model(view);
    world.add_data(new_value);

    update_world_state(world);
  }

  public Object edit_value_action(data_value dv) {
    if (update_collision()) {
      return new error_message("Editing item: version collision");
    }

    widget view = render_edit_value_page(dv, null, null);
    update_model(view);

    update_world_state(get_world());
    return null;
  }

  public Object publish_action(data_type dt) {
    data_value dv = get_current_value(dt);
    if (dv == null) {
      return new error_message("Not found item selection for " + dt.short_name());
    }
    return new text_content(resource_util.TEXT_HTML, new base_string(dt.publish(dv)));
  }

  public void import_action() {
    reference_wrapper<string> text = make_reference(null);
    update_model(import_view(text));
    if (text.get() == null) {
      return;
    }

    datastore_state new_state = get_marshaller().unmarshal_state(
        json_data.parse(text.get().unwrap()));

    if (new_state != null) {
      replace_world_state(new_state);
    }
  }

  private String make_function_declarations() {
    return utilities.s(text_utilities.to_plain_text(
        new reflect_util().render_world(get_schema().declaration)));
  }

  private String get_javascript_app() {
    StringBuilder sb = new StringBuilder();

    sb.append("var SYNC_REQUEST_URI = '");
    sb.append(request.to_uri(SYNC));
    sb.append("';\n\n");

    sb.append("var WORLD_SCHEMA_JSON = ");
    sb.append(utilities.s(get_marshaller().marshal_schema().stringify()));
    sb.append(";\n\n");

    sb.append("var WORLD_FLUID_CODE = {\n");
    sb.append(make_function_declarations());
    sb.append("};\n\n");

    sb.append("var WORLD_STATE_JSON = ");
    sb.append(utilities.s(marshal_world()));
    sb.append(";\n");

    return sb.toString();
  }

  public text_content javascript_page() {
    return to_html(new javascript(state.js_runtime().content + get_javascript_app()), true, null);
  }

  private static name WORLD_SCHEMA = new name("world_schema");
  private static name FLUID_CODE = new name("fluid_code");

  public string sync() {
    @Nullable string request_type = request.get(protocol.REQUEST_TYPE);
    @Nullable json_data payload = json_data.parse(request.get(protocol.PAYLOAD));

    if (request_type == null) {
      return new base_string("No request type specified");
    } else if (utilities.eq(request_type, protocol.PING)) {
      return new base_string("Response to ", request_type);
    } else if (utilities.eq(request_type, protocol.PULL) && payload != null) {
      string version_id = payload.get(marshaller.VERSION_ID);
      string source_version = payload.get(marshaller.SOURCE_VERSION);
      datastore_state world = get_world();
      boolean source_same = utilities.eq(world.get_source_version(), source_version);
      boolean data_same = utilities.eq(world.get_version_id(), version_id);
      if (source_same && data_same) {
        // No updates.
        return protocol.OK_RESPONSE;
      } else {
        marshaller the_marshaller = get_marshaller();
        json_data response = the_marshaller.marshal_state(world);
        if (!source_same) {
          response.add(WORLD_SCHEMA, the_marshaller.marshal_schema());
          response.add(FLUID_CODE, make_function_declarations());
        }
        return response.stringify();
      }
    } else if (utilities.eq(request_type, protocol.PUSH) && payload != null) {
      datastore_state new_state = get_marshaller().unmarshal_state(payload);
      if (new_state != null) {
        replace_world_state(new_state);
        return protocol.OK_RESPONSE;
      } else {
        return protocol.ERROR_RESPONSE;
      }
    } else {
      return new base_string("Unknown request " + request_type);
    }
  }

  public string msync() {
    @Nullable string request_type = request.get(protocol.REQUEST_TYPE);
    @Nullable json_data payload = json_data.parse(request.get(protocol.PAYLOAD));

    if (request_type == null) {
      return new base_string("No request type specified");
    } else if (utilities.eq(request_type, protocol.INIT)) {
      json_data response = get_marshaller().marshal_state(get_world());
      response.add(protocol.SOURCE, get_schema().source.content);
      return response.stringify();
    } else if (utilities.eq(request_type, protocol.PULL) && payload != null) {
      string version_id = payload.get(marshaller.VERSION_ID);
      string source_version = payload.get(marshaller.SOURCE_VERSION);
      datastore_state world = get_world();
      boolean source_same = utilities.eq(world.get_source_version(), source_version);
      boolean data_same = utilities.eq(world.get_version_id(), version_id);
      if (source_same && data_same) {
        // No updates.
        return protocol.OK_RESPONSE;
      } else {
        marshaller the_marshaller = get_marshaller();
        json_data response = the_marshaller.marshal_state(world);
        if (!source_same) {
          response.add(protocol.SOURCE, get_schema().source_content());
        }
        return response.stringify();
      }
    } else if (utilities.eq(request_type, protocol.PUSH) && payload != null) {
      datastore_state new_state = get_marshaller().unmarshal_state(payload);
      if (new_state != null) {
        replace_world_state(new_state);
        return protocol.OK_RESPONSE;
      } else {
        return protocol.ERROR_RESPONSE;
      }
    } else {
      return new base_string("Unknown request " + request_type);
    }
  }

  private marshaller get_marshaller() {
    return new marshaller(get_schema());
  }

  public static String to_display(String s) {
    if (s.isEmpty()) {
      return s;
    }

    StringBuilder sb = new StringBuilder();
    sb = sb.append(Character.toTitleCase(s.charAt(0)));

    for (int i = 1; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '_') {
        c = ' ';
      } else {
        c = Character.toLowerCase(c);
      }
      sb.append(c);
    }

    return sb.toString();
  }

  public static String to_display(identifier id) {
    assert id instanceof name || id instanceof simple_name;
    return to_display(utilities.s(id.to_string()));
  }
}

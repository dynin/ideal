-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.library.formats;
implicit import ideal.runtime.formats;
import ideal.machine.characters.unicode_handler;

--- Implement resource identifier associated with a |resource_store| and a path.
class base_resource_identifier {
  extends debuggable;
  implements resource_identifier;

  private resource_store the_resource_store;
  private string the_scheme;
  private immutable list[string] path;

  base_resource_identifier(resource_store the_resource_store, string the_scheme,
      immutable list[string] path) {
    this.the_resource_store = the_resource_store;
    this.the_scheme = the_scheme;
    this.path = path;
  }

  implement base_resource_identifier parent() {
    parent_path_size : path.size - 1;
    if (parent_path_size is nonnegative) {
      parent_path : path.slice(0, parent_path_size);
      return base_resource_identifier.new(the_resource_store, the_scheme,
          path.slice(0, parent_path_size));
    } else {
      return this;
    }
  }

  implement string scheme => the_scheme;

  implement string or null host() {
    -- Path starts with // and is then followed by host
    if (path.size > 2 && path[0].is_empty && path[1].is_empty) {
      return path[2];
    } else {
      return missing.instance;
    }
  }

  implement boolean exists() {
    return the_resource_store.exists(the_scheme, path);
  }

  override resource[string] access_string(access_option or null options) {
    return string_resource.new(this, options);
  }

  override resource[readonly json_data] access_json_data(access_option or null options) {
    return json_data_resource.new(this, options);
  }

  override resource_catalog access_catalog() {
    return base_resource_catalog.new(the_resource_store, the_scheme, path);
  }

  override string to_string() {
    return the_resource_store.build_name(the_scheme, path);
  }

  private string get_string() {
    return the_resource_store.read_string(the_scheme, path);
  }

  private set_string(string new_value, access_option or null options) {
    if (options is make_catalog_option && path.size > 1) {
      the_resource_store.make_catalog(the_scheme, parent.path);
    }
    the_resource_store.write_string(the_scheme, path, new_value);
  }

  private class string_resource {
    implements resource[string], reference[string];

    private base_resource_identifier the_identifier;
    private access_option or null options;

    string_resource(base_resource_identifier the_identifier, access_option or null options) {
      this.the_identifier = the_identifier;
      this.options = options;
    }

    override reference[string] content() {
      return this;
    }

    override string get() {
      return the_identifier.get_string();
    }

    override set(string new_value) {
      the_identifier.set_string(new_value, options);
    }

    override string to_string => the_identifier.to_string();
  }

  private class json_data_resource {
    implements resource[readonly json_data], reference[readonly json_data];


    private base_resource_identifier the_identifier;
    private access_option or null options;

    json_data_resource(base_resource_identifier the_identifier, access_option or null options) {
      this.the_identifier = the_identifier;
      this.options = options;
    }

    override reference[readonly json_data] content() {
      return this;
    }

    override readonly json_data get() {
      return json_parser.new(unicode_handler.instance).parse(the_identifier.get_string());
    }

    override set(readonly json_data new_data) {
      the string : json_printer.new(unicode_handler.instance).print(new_data);
      the_identifier.set_string(the_string, options);
    }

    override string to_string => the_identifier.to_string();
  }
}

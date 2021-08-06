-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

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

  override base_resource_identifier parent() {
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

  override boolean exists() {
    return the_resource_store.exists(the_scheme, path);
  }

  override resource[string] access_string(access_option or null options) {
    return string_resource.new(this, options);
  }

  override resource_catalog access_catalog() {
    return base_resource_catalog.new(the_resource_store, the_scheme, path);
  }

  override string to_string() {
    return the_resource_store.build_name(the_scheme, path);
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
      return the_identifier.the_resource_store.read_string(the_identifier.the_scheme,
          the_identifier.path);
    }

    override void set(string new_value) {
      if (options is make_catalog_option && the_identifier.path.size > 1) {
        the_identifier.the_resource_store.make_catalog(the_identifier.the_scheme,
            the_identifier.parent().path);
      }
      the_identifier.the_resource_store.write_string(the_identifier.the_scheme,
          the_identifier.path, new_value);
    }
  }
}

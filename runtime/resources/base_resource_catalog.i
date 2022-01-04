-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implements a resource catalog backed by a resource store.
class base_resource_catalog {
  implements resource_catalog, reference[dictionary[string, resource_identifier] or null];

  implicit import ideal.library.patterns;
  implicit import ideal.runtime.patterns;

  private static pattern[character] scheme_separator : singleton_pattern[character].new(':');
  private static pattern[character] path_separator : singleton_pattern[character].new('/');

  private resource_store the_resource_store;
  private string the_scheme;
  private immutable list[string] path;

  protected base_resource_catalog(resource_store the_resource_store, string the_scheme,
      immutable list[string] path) {
    this.the_resource_store = the_resource_store;
    this.the_scheme = the_scheme;
    this.path = path;
  }

  override reference[dictionary[string, resource_identifier] or null] content() {
    return this;
  }

  override dictionary[string, resource_identifier] or null get() {
    catalog : the_resource_store.read_catalog(the_scheme, path);
    if (catalog is null) {
      return missing.instance;
    }

    result : hash_dictionary[string, resource_identifier].new();
    for (resource_name : catalog.elements) {
      result.put(resource_name, resolve(resource_name));
    }

    return result;
  }

  override set(dictionary[string, resource_identifier] or null new_value) {
    utilities.panic("can't set a catalog");
  }

  implement resource_identifier id() {
    return base_resource_identifier.new(the_resource_store, the_scheme, path);
  }

  -- TODO: resource name parser should be separated from the catalog logic.
  -- We should just panic if we encounter special characters or names,
  -- such as '/', '\', '::', '.', '..'.
  overload implement resource_identifier resolve(string name) {
    if (name.is_empty) {
      return base_resource_identifier.new(the_resource_store, the_scheme, path);
    }

    scheme_range : scheme_separator.find_first(name, 0);

    if (scheme_range is_not null) {
      candidate_scheme : name.slice(0, scheme_range.begin);
      if (!the_resource_store.allow_scheme(candidate_scheme)) {
        utilities.panic("scheme " ++ candidate_scheme ++ " not allowed");
      }
      -- TODO: handle relative paths here
      components : path_separator.split(name.skip(scheme_range.end));
      -- TODO: cast is redundant
      return base_resource_identifier.new(the_resource_store, candidate_scheme,
          components.frozen_copy !> immutable list[string]);
    }

    -- TODO: we need to support other path separators for other platforms, such as \ on Windows.
    components : path_separator.split(name);

    var boolean absolute : false;
    var nonnegative index;
    result : base_list[string].new();

    if (components.first.is_empty) {
      if (the_resource_store.allow_up) {
        absolute = true;
      } else {
        -- TODO: log attempt to make absolute path
      }
      index = 1;
    } else {
      index = 0;
      result.append_all(path);
    }

    while (index < components.size) {
      -- TODO: cast should be redundant.
      component : components[index] !> string;
      index += 1;
      if (component.is_empty || component == resource_util.CURRENT_CATALOG) {
        -- skip dots.
        continue;
      } else if (component == resource_util.PARENT_CATALOG) {
        if (result.is_empty) {
          if (the_resource_store.allow_up) {
            result.append(component);
          } else {
            -- TODO: log attempt to break out of the catalog
          }
        } else {
          if (result.last == resource_util.PARENT_CATALOG) {
            assert the_resource_store.allow_up;
            result.append(component);
          } else {
            result.remove_last();
          }
        }
      } else {
        result.append(component);
      }
    }

    if (absolute) {
      result.prepend("");
    } else if (result.is_empty) {
      result.append(resource_util.CURRENT_CATALOG);
    }

    return base_resource_identifier.new(the_resource_store, the_scheme, result.frozen_copy);
  }

  overload implement resource_identifier resolve(string name, extension or null the_extension) {
    if (the_extension is_not null) {
      return resolve(base_string.new(name, the_extension.dot_name));
    } else {
      return resolve(name);
    }
  }

  override string to_string => id.to_string;
}

-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implements a resource catalog backed by a resource store.
class base_resource_catalog {
  implements resource_catalog;

  implicit import ideal.library.patterns;
  implicit import ideal.runtime.patterns;

  private static pattern[character] path_separator : singleton_pattern[character].new('/');

  private resource_store the_resource_store;
  private immutable list[string] path;

  protected base_resource_catalog(resource_store the_resource_store, immutable list[string] path) {
    this.the_resource_store = the_resource_store;
    this.path = path;
  }

  override resource_identifier get_id() {
    return base_resource_identifier.new(the_resource_store, path);
  }

  -- TODO: resource name parser should be separated from the catalog logic.
  -- We should just panic if we encounter special characters or names,
  -- such as '/', '\', '::', '.', '..'.
  override resource_identifier resolve(string name) {
    if (name.is_empty) {
      return base_resource_identifier.new(the_resource_store, path);
    }

    -- TODO: we need to support other path separators for other platforms, such as \ on Windows.
    components : path_separator.split(name);

    var boolean absolute : false;
    var nonnegative index;
    result : base_list[string].new();

    if (components[0].is_empty) {
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
      component : components[index] as string;
      index += 1;
      if (component.is_empty || component == resource_util.CURRENT_CATALOG) {
        -- skip dots.
        continue;
      } else if (component == resource_util.PARENT_CATALOG) {
        if (result.is_empty) {
          -- TODO: log attempt to break out of the catalog
        } else {
          result.remove_last();
        }
      } else {
        result.append(component);
      }
    }

    if (absolute) {
      result.prepend("");
    }

    return base_resource_identifier.new(the_resource_store, result.frozen_copy());
  }

  override resource_identifier resolve(string name, extension or null the_extension) {
    if (the_extension is_not null) {
      return resolve(base_string.new(name, the_extension.dot_name));
    } else {
      return resolve(name);
    }
  }
}

-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.channels.string_writer;

--- Implements resource identifier resolution logic shared by implementations
--- of |resource_stores|s.
abstract class base_resource_store {
  extends debuggable;
  implements resource_store;

  private string path_prefix;
  private boolean do_allow_up;
  private boolean skip_prefix;

  protected base_resource_store(string path_prefix, boolean do_allow_up, boolean skip_prefix) {
    this.path_prefix = path_prefix;
    this.do_allow_up = do_allow_up;
    this.skip_prefix = skip_prefix;
  }

  override boolean allow_up() {
    return do_allow_up;
  }

  override string build_name(immutable list[string] path) {
    if (path.is_empty) {
      return path_prefix;
    }

    result : string_writer.new();
    if (!skip_prefix) {
      result.write_all(path_prefix);
    }

    -- TODO: use list.join()
    for (var nonnegative i : 0; i < path.size; i += 1) {
      if (i > 0) {
        result.write_all(resource_util.PATH_SEPARATOR);
      }
      result.write_all(path[i]);
    }

    return result.elements();
  }

  public resource_catalog top() {
    return base_resource_catalog.new(this, empty[string].new());
  }
}

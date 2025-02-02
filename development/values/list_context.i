-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class list_context {
  implements variable_context;

  private final readonly list[any value] the_list;

  list_context(readonly list[any value] the_list) {
    this.the_list = the_list;
  }

  override put_var(variable_id key, value_wrapper value) {
    utilities.panic("list_context.put_var() for " ++ key);
  }

  override value_wrapper get_var(variable_id key) {
    if (key.short_name == common_names.size_name) {
      return integer_value.new(the_list.size, common_types.immutable_nonnegative_type);
    }

    utilities.panic("list_context.get_var() for " ++ key);
  }
}

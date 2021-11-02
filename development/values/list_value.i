-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class list_value {
  extends debuggable;
  implements composite_wrapper[any list[value_wrapper]];

  any list[value_wrapper] the_list;
  private type bound;

  list_value(any list[value_wrapper] the_list, type bound) {
    this.the_list = the_list;
    this.bound = bound;
  }

  override
  type type_bound() {
    return bound;
  }

  override
  any list[value_wrapper] unwrap() {
    return the_list;
  }

  override value_wrapper get_var(variable_id key) {
    if (key.short_name == common_names.size_name) {
      return integer_value.new((the_list !> readonly list[value_wrapper]).size,
          common_types.immutable_nonnegative_type);
    }

    utilities.panic("Failing list_value.get_var() for " ++ key);
  }

  override void put_var(variable_id key, value_wrapper value) {
    utilities.panic("Failing list_value.put_var() for " ++ key);
  }
}

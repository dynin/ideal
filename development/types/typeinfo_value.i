-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class typeinfo_value {
  extends debuggable;
  implements value_wrapper;

  private type the_type;

  typeinfo_value(type the_type) {
    this.the_type = the_type;
  }

  type get_type() => the_type;

  -- TODO: shouldn't we return a metatype here?
  implement type type_bound => the_type;

  implement value unwrap => the_type;

  implement string to_string => "typeinfo-value: " ++ the_type;
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class typeinfo_value {
  extends debuggable;
  implements value_wrapper;

  private type the_type;

  typeinfo_value(type the_type) {
    this.the_type = the_type;
  }

  type get_type() => the_type;

  -- TODO: shouldn't we return a metatype here?
  override type type_bound => the_type;

  override value unwrap => the_type;

  override string to_string => "typeinfo-value: " ++ the_type;
}

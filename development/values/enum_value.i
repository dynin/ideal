-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class enum_value {
  extends base_data_value;
  implements identifier;

  private variable_declaration the_declaration;
  private nonnegative ordinal;

  enum_value(variable_declaration the_declaration, nonnegative ordinal, type bound) {
    super(bound);
    this.the_declaration = the_declaration;
    this.ordinal = ordinal;
    assert the_declaration.short_name is simple_name;
  }

  var simple_name short_name => the_declaration.short_name !> simple_name;

  declaration get_declaration => the_declaration;

  override string to_string => short_name.to_string();
}
-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Generic token type.
class base_token_type {
  implements token_type, readonly displayable;
  extends debuggable;

  private final string the_name;
  dont_display private final integer the_symbol;

  overload base_token_type(string name, integer the_symbol) {
    assert name.is_not_empty;
    this.the_name = name;
    this.the_symbol = the_symbol;
  }

  overload base_token_type(string name) {
    this(name, -1);
  }

  override string name => the_name;

  override integer symbol => the_symbol;

  override string to_string => "\"" ++ the_name ++ "\"";

  override string display() => the_name;
}

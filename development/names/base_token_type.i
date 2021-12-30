-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Generic token type.
class base_token_type {
  implements token_type, readonly displayable;
  extends debuggable;

  private string the_name;
  dont_display private string the_symbol_identifier;

  base_token_type(string name, string the_symbol_identifier) {
    assert name.is_not_empty;
    this.the_name = name;
    this.the_symbol_identifier = the_symbol_identifier;
  }

  override string name => the_name;

  override string symbol_identifier => the_symbol_identifier;

  override string to_string => "\"" ++ the_name ++ "\"";

  override string display() => the_name;
}

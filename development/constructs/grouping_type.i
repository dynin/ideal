-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.runtime.logs;
import ideal.development.names.punctuation;

-- TODO: use auto_constructor
class grouping_type {
  implements identifier, readonly displayable;

  string name;
  token_type start;
  token_type end;

  private grouping_type(string name, token_type start, token_type end) {
    this.name = name;
    this.start = start;
    this.end = end;
  }

  override string to_string => name;

  override public string display => to_string;

  static PARENS :
      grouping_type.new("parens", punctuation.OPEN_PARENTHESIS, punctuation.CLOSE_PARENTHESIS);
  static BRACKETS :
      grouping_type.new("brackets", punctuation.OPEN_BRACKET, punctuation.CLOSE_BRACKET);
  static ANGLE_BRACKETS :
      grouping_type.new("angle_brackets", punctuation.LESS_THAN, punctuation.GREATER_THAN);
  static BRACES :
      grouping_type.new("braces", punctuation.OPEN_BRACE, punctuation.CLOSE_BRACE);
}

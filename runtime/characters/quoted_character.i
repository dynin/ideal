-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class quoted_character {
  implements deeply_immutable data, stringable;

  static ESCAPE : '\\';

  string name;
  character name_character;
  character value_character;
  nonnegative ascii_code;

  -- TODO: use auto_constructor
  public quoted_character(string name, character name_character, character value_character,
      nonnegative ascii_code) {
    this.name = name;
    this.name_character = name_character;
    this.value_character = value_character;
    this.ascii_code = ascii_code;
  }

  -- TODO: cache
  var string with_escape => ESCAPE ++ name_character;

  -- TODO: cache
  implement string to_string => '<' ++ name ++ '>';

  static readonly list[quoted_character] json_list : [
    quoted_character.new("backspace",       'b',    '\b',   0x08),
    quoted_character.new("formfeed",        'f',    '\f',   0x0C),
    quoted_character.new("newline",         'n',    '\n',   0x0A),
    quoted_character.new("carriage return", 'r',    '\r',   0x0D),
    quoted_character.new("horizontal tab",  't',    '\t',   0x09),
    quoted_character.new("double quote",    '"',    '"',    0x22),
    quoted_character.new("slash",           '/',    '/',    0x2F),
    quoted_character.new("backslash",       ESCAPE, ESCAPE, 0x5C),
  ];

  static var readonly list[quoted_character] all_list;

  static {
    the_list : base_list[quoted_character].new();
    the_list.append_all(json_list);
    the_list.append(quoted_character.new("single quote", '\'', '\'', 0x27));
    all_list = the_list;
  }
}

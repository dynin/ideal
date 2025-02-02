-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

auto_constructor class quoted_character {
  implements deeply_immutable data, stringable;

  static ESCAPE : '\\';

  string name;
  character name_character;
  character value_character;
  nonnegative ascii_code;

  -- TODO: cache
  var string with_escape => ESCAPE ++ name_character;

  -- TODO: cache
  implement string to_string => '<' ++ name ++ '>';

  -- TODO: reformat; convert to enum?
  static quoted_character backspace       : quoted_character.new("backspace",       'b',    '\b',   0x08);
  static quoted_character formfeed        : quoted_character.new("formfeed",        'f',    '\f',   0x0C);
  static quoted_character newline         : quoted_character.new("newline",         'n',    '\n',   0x0A);
  static quoted_character carriage_return : quoted_character.new("carriage return", 'r',    '\r',   0x0D);
  static quoted_character horizontal_tab  : quoted_character.new("horizontal tab",  't',    '\t',   0x09);
  static quoted_character single_quote    : quoted_character.new("single quote",    '\'',   '\'',   0x27);
  static quoted_character double_quote    : quoted_character.new("double quote",    '"',    '"',    0x22);
  static quoted_character slash           : quoted_character.new("slash",           '/',    '/',    0x2F);
  static quoted_character backslash       : quoted_character.new("backslash",       ESCAPE, ESCAPE, 0x5C);

  static readonly list[quoted_character] all_list : [
    backspace,
    formfeed,
    newline,
    carriage_return,
    horizontal_tab,
    single_quote,
    double_quote,
    slash,
    backslash
  ];

  static readonly list[quoted_character] json_list : [
    backspace,
    formfeed,
    newline,
    carriage_return,
    horizontal_tab,
    double_quote,
    slash,
    backslash
  ];

  static readonly list[quoted_character] java_list : [
    backspace,
    formfeed,
    newline,
    carriage_return,
    horizontal_tab,
    single_quote,
    double_quote,
    backslash
  ];
}

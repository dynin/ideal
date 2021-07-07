-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- JSON parser implementation.
class json_parser {
--  implicit import ideal.library.patterns;
--  implicit import ideal.runtime.patterns;

  character_handler the_character_handler;
  -- TODO: use data instead of value
  var list[immutable value] tokens;
  var string or null error;

  json_parser(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    tokens = base_list[immutable value].new();
  }

  void tokenize(string input) {
    var nonnegative start : 0;
    while (start < input.size && error is null) {
      start = scan(input, start);
    }
  }

--  TODO: drop this.
--  private boolean is_whitespace(character the_character) {
--    return the_character == ' ' ||
--           the_character == '\n' ||
--           the_character == '\r' ||
--           the_character == '\t';
--  }

  private nonnegative scan(string input, var nonnegative start) {
    next : input[start];

    if (the_character_handler.is_whitespace(next)) {
      start += 1;
      while (start < input.size && the_character_handler.is_whitespace(input[start])) {
        start += 1;
      }
      return start;
    }

    if (next == '"') {
      start += 1;
      string_start : start;
      while (start < input.size) {
        next_in_string : input[start];
        if (next_in_string == '"') {
          tokens.append(input.slice(string_start, start));
          return start + 1;
        }
        start += 1;
      }
      report_error("No closing quote in a string");
      return start;
    }

    report_error("Unrecognized character in a string: " ++ next);
    return start + 1;
  }

  private void report_error(string message) {
    error = message;
  }
}

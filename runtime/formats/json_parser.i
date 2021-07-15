-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- JSON parser implementation.
class json_parser {
--  implicit import ideal.runtime.formats.json_token;

  character_handler the_character_handler;
  -- TODO: use data instead of equality_comparable
  var list[readonly data] tokens;
  var string or null error;

  json_parser(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    tokens = base_list[readonly data].new();
  }

  boolean has_error => error is_not null;

  private void tokenize(string input) {
    tokens.clear();
    var nonnegative start : 0;
    while (start < input.size && error is null) {
      start = scan(input, start);
    }
  }

  list[readonly data] test_tokenize(string input) {
    tokenize(input);
    assert !has_error();
    return tokens;
  }

  private nonnegative scan(string input, var nonnegative start) {
    next : input[start];
    start += 1;

    if (the_character_handler.is_whitespace(next)) {
      while (start < input.size && the_character_handler.is_whitespace(input[start])) {
        start += 1;
      }
      return start;
    }

    if (next == '"') {
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

    if (the_character_handler.is_digit(next)) {
      digit : the_character_handler.from_digit(next, radix.DEFAULT_RADIX);
      assert digit is nonnegative;
      var nonnegative result : digit;
      while (start < input.size && the_character_handler.is_digit(input[start])) {
        next_digit : the_character_handler.from_digit(input[start], radix.DEFAULT_RADIX);
        assert next_digit is nonnegative;
        result = result * radix.DEFAULT_RADIX + next_digit;
        start += 1;
      }
      -- TODO: handle fraction and exponent
      tokens.append(result);
      return start;
    }

    -- TODO: iterate over json_tokens
    if (next == json_token.OPEN_BRACE.token) {
      tokens.append(json_token.OPEN_BRACE);
      return start;
    }

    if (next == json_token.CLOSE_BRACE.token) {
      tokens.append(json_token.CLOSE_BRACE);
      return start;
    }

    if (next == json_token.OPEN_BRACKET.token) {
      tokens.append(json_token.OPEN_BRACKET);
      return start;
    }

    if (next == json_token.CLOSE_BRACKET.token) {
      tokens.append(json_token.CLOSE_BRACKET);
      return start;
    }

    if (next == json_token.COMMA.token) {
      tokens.append(json_token.COMMA);
      return start;
    }

    if (next == json_token.COLON.token) {
      tokens.append(json_token.COLON);
      return start;
    }

    report_error("Unrecognized character in a string: " ++ next);
    return start;
  }

  private void report_error(string message) {
    error = message;
  }
}

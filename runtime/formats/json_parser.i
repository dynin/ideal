-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- JSON parser implementation.
class json_parser {
--  implicit import ideal.runtime.formats.json_token;
  import ideal.runtime.patterns.list_pattern;
  import ideal.machine.channels.string_writer;

  auto_constructor class parse_result {
    readonly json_data the_json_data;
    nonnegative end_index;
  }

  character_handler the_character_handler;
  -- TODO: use data instead of equality_comparable
  var list[readonly data] tokens;
  var string or null error;

  json_parser(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    tokens = base_list[readonly data].new();
  }

  boolean has_error => error is_not null;

  private tokenize(string input) {
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

  readonly json_data parse(string input) {
    tokenize(input);
    assert !has_error();
    result : parse_value(0);
    assert !has_error();
    return result.the_json_data;
  }

  private nonnegative scan(string input, nonnegative start) {
    next : input[start];
    var nonnegative index : start + 1;

    if (the_character_handler.is_whitespace(next)) {
      while (index < input.size && the_character_handler.is_whitespace(input[index])) {
        index += 1;
      }
      return index;
    }

    if (next == '"') {
      result : string_writer.new();
      while (index < input.size) {
        next_in_input : input[index];
        if (next_in_input == '"') {
          tokens.append(result.elements);
          return index + 1;
        } else if (next_in_input == quoted_character.ESCAPE) {
          if (index >= input.size) {
            report_error("Escape at the end of input");
            return index;
          }
          index += 1;
          escaped_character : input[index];
          if (escaped_character == 'u') {
            if (index + 4 >= input.size) {
              report_error("Unicode escape at the end of input");
              return index;
            }
            var code : hex_digit(input[index + 1]);
            code = code * 16 + hex_digit(input[index + 2]);
            code = code * 16 + hex_digit(input[index + 3]);
            code = code * 16 + hex_digit(input[index + 4]);
            result.write(the_character_handler.from_code(code));
            index += 4;
          } else {
            var found : false;
            for (quoted : quoted_character.json_list) {
              if (escaped_character == quoted.name_character) {
                result.write(quoted.value_character);
                found = true;
                break;
              }
            }
            if (!found) {
              report_error("Unrecognized escape character: " ++ escaped_character);
              return index;
            }
          }
        } else {
          result.write(next_in_input);
        }
        index += 1;
      }
      report_error("No closing quote in a string");
      return index;
    }

    if (the_character_handler.is_digit(next)) {
      return scan_number(input, start, false);
    }

    -- TODO: handle json tokens (below) in the switch
    switch (next) {
      case '-':
        if (index < input.size) {
          return scan_number(input, index, true);
        } else {
          report_error("Minus at the end of input");
          return index;
        }
      case 't':
        return scan_symbol(input, start, "true", true);
      case 'f':
        return scan_symbol(input, start, "false", false);
      case 'n':
        -- TODO: use native_null or another flavor of null other than missing
        return scan_symbol(input, start, "null", missing.instance);
    }

    -- TODO: iterate over json_tokens
    if (next == json_token.OPEN_BRACE.the_character) {
      tokens.append(json_token.OPEN_BRACE);
      return index;
    }

    if (next == json_token.CLOSE_BRACE.the_character) {
      tokens.append(json_token.CLOSE_BRACE);
      return index;
    }

    if (next == json_token.OPEN_BRACKET.the_character) {
      tokens.append(json_token.OPEN_BRACKET);
      return index;
    }

    if (next == json_token.CLOSE_BRACKET.the_character) {
      tokens.append(json_token.CLOSE_BRACKET);
      return index;
    }

    if (next == json_token.COMMA.the_character) {
      tokens.append(json_token.COMMA);
      return index;
    }

    if (next == json_token.COLON.the_character) {
      tokens.append(json_token.COLON);
      return index;
    }

    report_error("Unrecognized character in a string: " ++ next);
    return index;
  }

  private nonnegative hex_digit(character the_character) {
    result : the_character_handler.from_digit(the_character, 16);
    if (result is nonnegative) {
      return result;
    } else {
      report_error("Unrecognized character in hex escape: " ++ the_character);
      return 0;
    }
  }

  private nonnegative scan_number(string input, nonnegative start, boolean negate) {
    next : input[start];
    if (!the_character_handler.is_digit(next)) {
      report_error("Unrecognized digit: " ++ next);
      return start;
    }

    digit : the_character_handler.from_digit(next, radixes.DEFAULT_RADIX);
    assert digit is nonnegative;
    var nonnegative result : digit;
    var index : start + 1;
    while (index < input.size && the_character_handler.is_digit(input[index])) {
      next_digit : the_character_handler.from_digit(input[index], radixes.DEFAULT_RADIX);
      assert next_digit is nonnegative;
      result = result * radixes.DEFAULT_RADIX + next_digit;
      index += 1;
    }

    -- TODO: handle fraction and exponent
    tokens.append(negate ? -result : result);
    return index;
  }

  private nonnegative scan_symbol(string input, nonnegative start, string symbol,
      deeply_immutable data value) {
    prefix : list_pattern[character].new(symbol).match_prefix(input.skip(start));
    -- TODO: use && to join checks
    if (prefix is_not null) {
      if (prefix == symbol.size) {
        tokens.append(value);
        return start + prefix;
      }
    }

    report_error("Can't scan symbol: " ++ symbol);
    return start + 1;
  }

  private parse_result parse_value(nonnegative start) {
    if (start >= tokens.size) {
      return parse_error("End of tokens when parsing value");
    }

    next : tokens[start];

    if (next == json_token.OPEN_BRACE) {
      return parse_object(start);
    } else if (next == json_token.OPEN_BRACKET) {
      return parse_array(start);
    }

    if (next is json_token) {
      return parse_error("Unexpected token: " ++ next);
    }

    return parse_result.new(next !> readonly json_data, start + 1);
  }

  private parse_result parse_object(nonnegative start) {
    if (tokens[start] != json_token.OPEN_BRACE) {
      return parse_error("Open brace expected");
    }

    json_object result : json_object_impl.new();
    var index : start + 1;

    while (index < tokens.size) {
      next : tokens[index];
      if (next == json_token.CLOSE_BRACE) {
        return parse_result.new(result, index + 1);
      }
      if (next is_not string) {
        return parse_error("Expected string identifier in object");
      }
      index += 1;
      while (index >= tokens.size || tokens[index] != json_token.COLON) {
        return parse_error("Expected colon in object");
      }
      index += 1;
      element : parse_value(index);
      if (has_error()) {
        return element;
      }
      result.put(next, element.the_json_data);
      index = element.end_index;
      if (index >= tokens.size) {
        return parse_error("No closing brace in object");
      }
      if (tokens[index] == json_token.CLOSE_BRACE) {
        return parse_result.new(result, index + 1);
      }
      if (tokens[index] != json_token.COMMA) {
        return parse_error("Expected comma in object");
      }
      index += 1;
    }

    return parse_error("No closing brace in object");
  }

  private parse_result parse_array(nonnegative start) {
    if (tokens[start] != json_token.OPEN_BRACKET) {
      return parse_error("Open bracket expected");
    }

    json_array result : json_array_impl.new();
    var index : start + 1;

    while (index < tokens.size) {
      if (tokens[index] == json_token.CLOSE_BRACKET) {
        return parse_result.new(result, index + 1);
      }
      element : parse_value(index);
      if (has_error()) {
        return element;
      }
      result.append(element.the_json_data);
      index = element.end_index;
      if (index >= tokens.size) {
        return parse_error("No closing bracket in array");
      }
      if (tokens[index] == json_token.CLOSE_BRACKET) {
        return parse_result.new(result, index + 1);
      }
      if (tokens[index] != json_token.COMMA) {
        return parse_error("Expected comma in array");
      }
      index += 1;
    }

    return parse_error("No closing bracket in array");
  }

  private report_error(string message) {
    error = message;
  }

  private parse_result parse_error(string message) {
    report_error(message);
    return parse_result.new(message, 0);
  }
}

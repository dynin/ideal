-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- JSON printer implementation.
class json_printer {
  import ideal.machine.channels.string_writer;

  character_handler the_character_handler;

  json_printer(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
  }

  string print(readonly json_data the_json_data) {
    result : string_writer.new();
    print_data(the_json_data, result);
    return result.elements();
  }

  private void print_data(readonly json_data the_json_data, string_writer result) {
    if (the_json_data is string) {
      print_string(the_json_data, result);
    } else if (the_json_data is integer) {
      print_integer(the_json_data, result);
    } else if (the_json_data is readonly json_array) {
      print_array(the_json_data, result);
    } else if (the_json_data is readonly json_object) {
      print_object(the_json_data, result);
    } else if (the_json_data is boolean) {
      print_boolean(the_json_data, result);
    } else if (the_json_data is null) {
      result.write_all("null");
    } else {
      -- TODO: introduce displayable
      utilities.panic("Unknown JSON data value");
    }
  }

  private void print_string(string the_string, string_writer result) {
    result.write('"');
    for (the_character : the_string) {
      if (the_character == '"' ||
          the_character == '\\' ||
          the_character == '/') {
        result.write('\\');
        result.write(the_character);
      } else if (the_character == '\b') {
        result.write_all("\\b");
      } else if (the_character == '\f') {
        result.write_all("\\f");
      } else if (the_character == '\n') {
        result.write_all("\\n");
      } else if (the_character == '\r') {
        result.write_all("\\r");
      } else if (the_character == '\t') {
        result.write_all("\\t");
      } else {
        result.write(the_character);
      }
    }
    result.write('"');
  }

  private void print_integer(integer the_integer, string_writer result) {
    result.write_all(the_integer.to_string);
  }

  private void print_array(readonly json_array the_array, string_writer result) {
    result.write(json_token.OPEN_BRACKET.the_character);
    var start : true;
    -- TODO: implement list.join()
    for (element : the_array) {
      if (start) {
        start = false;
      } else {
        result.write(json_token.COMMA.the_character);
        result.write(' ');
      }
      print_data(element, result);
    }
    result.write(json_token.CLOSE_BRACKET.the_character);
  }

  private void print_object(readonly json_object the_object, string_writer result) {
    result.write(json_token.OPEN_BRACE.the_character);
    var start : true;
    for (element : the_object.elements) {
      if (start) {
        start = false;
      } else {
        result.write(json_token.COMMA.the_character);
        result.write(' ');
      }
      print_string(element.key, result);
      result.write(json_token.COLON.the_character);
      result.write(' ');
      print_data(element.value, result);
    }
    result.write(json_token.CLOSE_BRACE.the_character);
  }

  private void print_boolean(boolean the_boolean, string_writer result) {
    -- TODO: use the_boolean.to_string
    result.write_all(the_boolean ++ "");
  }
}

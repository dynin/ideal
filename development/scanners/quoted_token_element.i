-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class quoted_token_element {
  extends base_scanner_element;

  private quote_type the_quote_type;

  quoted_token_element(quote_type the_quote_type) {
    this.the_quote_type = the_quote_type;
  }

  private var character quote_character => the_quote_type.quote_character;

  override scan_state or null process(source_content source, nonnegative begin) {
    string input : source.content;
    if (input[begin] != quote_character) {
      return missing.instance;
    }
    result : base_list[literal_fragment].new();
    value : string_writer.new();
    var start_index : begin + 1;
    var index : start_index;
    for (; index < input.size; index += 1) {
      var the character : input[index];
      if (the_character == quote_character) {
        break;
      }
      -- Make efficient when there are no escapes.
      if (the_character == quoted_character.ESCAPE) {
        if (start_index < index) {
          result.append(string_fragment.new(input.slice(start_index, index)));
        }
        index += 1;
        -- TODO: handle escape at the end of file.
        assert index < input.size;
        the_character = input[index];

        var found : false;
        -- Support other escape chars, such as code escapes
        for (quoted : quoted_character.java_list) {
          if (the_character == quoted.name_character) {
            the_character = quoted.value_character;
            result.append(quoted_fragment.new(quoted));
            start_index = index + 1;
            found = true;
            break;
          }
        }
        if (!found) {
          -- TODO: convert to an error
          utilities.panic("Unknown quoted char " ++ the_character);
        }
      }
      value.write(the_character);
    }
    if (start_index < index) {
      result.append(string_fragment.new(input.slice(start_index, index)));
    }
    var nonnegative image_end;
    if (index == input.size) {
      start_origin : source.make_origin(begin, begin + 1);
      eof_origin : source.make_origin(index, index);
      notification open_message : base_notification.new(messages.opening_quote, start_origin);
      base_notification.new(messages.quote_not_found, eof_origin, [ open_message, ]).report();
      image_end = index;
    } else {
      image_end = index + 1;
    }
    the origin : source.make_origin(begin, image_end);
    -- TODO: retire value from string_literal constructor.
    the string_literal : string_literal.new(value.elements, result.frozen_copy, the_quote_type);
    return scan_state.new(base_token[string_literal].new(special_token_type.LITERAL,
        the_string_literal, the_origin), begin + 1, image_end);
  }
}

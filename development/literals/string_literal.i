-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class string_literal {
  extends debuggable;
  implements literal[string];

  string string_value;
  immutable list[literal_fragment] content;
  quote_type quote;

  overload string_literal(string string_value, immutable list[literal_fragment] content,
      quote_type quote) {
    this.string_value = string_value;
    this.content = content;
    this.quote = quote;
  }

  overload string_literal(string string_value, quote_type quote) {
    this.string_value = string_value;
    this.content = escape_content(string_value);
    this.quote = quote;
  }

  override string the_value => string_value;

  -- TODO: cache result
  string content_with_escapes() {
    the_string_writer : string_writer.new();
    for (fragment : content) {
      the_string_writer.write_all(fragment.to_string);
    }
    return the_string_writer.elements;
  }

  override string to_string => content_with_escapes();

  private static immutable list[literal_fragment] escape_content(string input) {
    result : base_list[literal_fragment].new();
    var nonnegative start_index : 0;
    for (index : input.indexes) {
      the_character : input[index];
      for (quoted : quoted_character.java_list) {
        if (the_character == quoted.value_character) {
          if (start_index < index) {
            result.append(string_fragment.new(input.slice(start_index, index)));
          }
          result.append(quoted_fragment.new(quoted));
          start_index = index + 1;
          break;
        }
      }
    }
    if (start_index < input.size) {
      result.append(string_fragment.new(input.skip(start_index)));
    }
    return result.frozen_copy;
  }
}

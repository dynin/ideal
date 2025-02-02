-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class integer_token_element {
  extends base_scanner_element;

  override scan_state or null process(source_content source, nonnegative begin) {
    input : source.content;
    assert begin < input.size;
    first : input[begin];
    if (!the_character_handler.is_digit(first)) {
      return missing.instance;
    }

    var nonnegative end : begin + 1;
    var nonnegative radix;
    var nonnegative value;
    if (end < input.size && the_character_handler.to_lower_case(input[end]) == 'x') {
      radix = 16;
      value = 0;
      end += 1;
    } else {
      radix = radixes.DEFAULT_RADIX;
      digit : the_character_handler.from_digit(first, radix);
      assert digit is nonnegative;
      value = digit;
    }

    while (end < input.size) {
      digit : the_character_handler.from_digit(input[end], radix);
      if (digit is null) {
        break;
      }
      value = value * radix + digit;
      end += 1;
    }

    image : input.slice(begin, end);
    the origin : source.make_origin(begin, end);
    int_literal : integer_literal.new(value, image, radix);
    return scan_state.new(base_token[integer_literal].new(special_token_type.LITERAL,
        int_literal, the_origin), end, end);
  }
}

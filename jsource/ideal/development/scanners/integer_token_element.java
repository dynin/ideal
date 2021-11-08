/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;

public class integer_token_element extends base_scanner_element {
  @Override
  public scan_state process(source_content source, int begin) {
    String input = utilities.s(source.content);
    assert begin < input.length();
    char first = input.charAt(begin);
    if (!the_character_handler().is_digit(first)) {
      return null;
    }

    int end = begin + 1;
    int radix;
    int value;
    if (end < input.length() && the_character_handler().to_lower_case(input.charAt(end)) == 'x') {
      radix = 16;
      value = 0;
      end += 1;
    } else {
      radix = radixes.DEFAULT_RADIX;
      value = the_character_handler().from_digit(first, radix);
    }

    while (end < input.length()) {
      char c = input.charAt(end);
      Integer digit = the_character_handler().from_digit(c, radix);
      if (digit == null) {
        break;
      }
      value = value * radix + digit;
      end += 1;
    }

    String image = input.substring(begin, end);
    origin pos = source.make_origin(begin, end);
    integer_literal int_literal = new integer_literal(value, new base_string(image), radix);
    return new scan_state(
        new base_token<literal>(special_token_type.LITERAL, int_literal, pos), end, end);
  }
}

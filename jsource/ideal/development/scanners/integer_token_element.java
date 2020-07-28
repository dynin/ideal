/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;

public class integer_token_element implements scanner_element {
  @Override
  public scan_state process(source_content source, int begin) {
    String input = utilities.s(source.content);
    int end;
    for (end = begin; end < input.length(); ++end) {
      if (!Character.isDigit(input.charAt(end))) {
        break;
      }
    }
    if (end == begin) {
      return null;
    }
    String image = input.substring(begin, end);
    origin pos = source.make_origin(begin, end);
    int value = 0;
    try {
      value = Integer.parseInt(image);
    } catch (NumberFormatException e) {
      new base_notification(messages.number_format_error, pos).report();
    }
    integer_literal int_literal = new integer_literal(value, new base_string(image));
    return new scan_state(
        new base_token<literal>(special_token_type.LITERAL, int_literal, pos), end, end);
  }
}

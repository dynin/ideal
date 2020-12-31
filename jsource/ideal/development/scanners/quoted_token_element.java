/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;

public class quoted_token_element implements scanner_element {
  private final token_type the_token_type;
  private final char quote_character;

  private static final char ESCAPE = '\\';

  public quoted_token_element(token_type the_token_type) {
    this.the_token_type = the_token_type;
    string name = this.the_token_type.name();
    assert name.size() == 1;
    quote_character = name.first();
  }

  // TODO: FIX ESCAPING!!!
  @Override
  public scan_state process(source_content source, int begin) {
    String input = utilities.s(source.content);
    if (input.charAt(begin) != quote_character) {
      return null;
    }
    StringBuilder value = new StringBuilder();
    int end;
    for (end = begin + 1; end < input.length(); ++end) {
      char c = input.charAt(end);
      if (c == quote_character) {
        break;
      }
      if (c == ESCAPE) {
        // Support other escape chars.
        // Make efficient when there are no escapes.
        ++end;
        assert end < input.length();
        c = input.charAt(end);
        switch (c) {
          case 'n':
            c = '\n';
            break;
          case '\'':
          case '"':
          case '\\':
            break;
          default:
            // TODO: convert to an error
            throw new RuntimeException("Unknown quoted char " + c);
        }
      }
      value.append(c);
    }
    int image_end;
    if (end == input.length()) {
      origin start_pos = source.make_origin(begin, begin + 1);
      origin eof_pos = source.make_origin(end, end);
      notification open_message = new base_notification(
          messages.opening_quote, start_pos);
      list<notification> subnotifications =
          new base_list<notification>(open_message);
      new base_notification(messages.quote_not_found, eof_pos, subnotifications).report();

      image_end = end;
    } else {
      image_end = end + 1;
    }
    String image = input.substring(begin, image_end);
    origin pos = source.make_origin(begin, image_end);
    string quoted = new base_string(input.substring(begin + 1, end));
    quoted_literal string_literal = new quoted_literal(new base_string(value.toString()),
        quoted, the_token_type);
    return new scan_state(new base_token<literal>(special_token_type.LITERAL, string_literal, pos),
        begin + 1, image_end);
  }
}

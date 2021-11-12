-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;

public class quoted_token_element extends base_scanner_element {
  private final quote_type the_quote_type;

  public quoted_token_element(quote_type the_quote_type) {
    this.the_quote_type = the_quote_type;
  }

  private char quote_character() {
    return the_quote_type.quote_character;
  }

  // TODO: FIX ESCAPING!!!
  @Override
  public scan_state process(source_content source, int begin) {
    string input = source.content;
    if (input.get(begin) != quote_character()) {
      return null;
    }
    list<literal_fragment> result = new base_list<literal_fragment>();
    StringBuilder value = new StringBuilder();
    int start_index = begin + 1;
    int index = start_index;
    for (; index < input.size(); ++index) {
      char the_character = input.get(index);
      if (the_character == quote_character()) {
        break;
      }
      // Make efficient when there are no escapes.
      if (the_character == quoted_character.ESCAPE) {
        if (start_index < index) {
          result.append(new string_fragment(input.slice(start_index, index)));
        }
        ++index;
        // TODO: handle escape at the end of file.
        assert index < input.size();
        the_character = input.get(index);

        boolean found = false;
        // Support other escape chars.
        readonly_list<quoted_character> quoted_list = quoted_character.java_list;
        for (int quoted_index = 0; quoted_index < quoted_list.size(); quoted_index += 1) {
          quoted_character quoted = quoted_list.get(quoted_index);
          if (the_character == quoted.name_character) {
            the_character = quoted.value_character;
            result.append(new quoted_fragment(quoted));
            start_index = index + 1;
            found = true;
            break;
          }
        }
        if (!found) {
          // TODO: convert to an error
          throw new RuntimeException("Unknown quoted char " + the_character);
        }
      }
      value.append(the_character);
    }
    if (start_index < index) {
      result.append(new string_fragment(input.slice(start_index, index)));
    }
    int image_end;
    if (index == input.size()) {
      origin start_pos = source.make_origin(begin, begin + 1);
      origin eof_pos = source.make_origin(index, index);
      notification open_message = new base_notification(
          messages.opening_quote, start_pos);
      list<notification> subnotifications =
          new base_list<notification>(open_message);
      new base_notification(messages.quote_not_found, eof_pos, subnotifications).report();

      image_end = index;
    } else {
      image_end = index + 1;
    }
    origin pos = source.make_origin(begin, image_end);
    // TODO: retire value from string_literal constructor.
    string_literal string_literal = new string_literal(new base_string(value.toString()),
        result.frozen_copy(), the_quote_type);
    return new scan_state(new base_token<literal>(special_token_type.LITERAL, string_literal, pos),
        begin + 1, image_end);
  }
}

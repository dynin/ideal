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
import ideal.development.elements.*;
import ideal.development.constructs.jump_category;
import ideal.development.comments.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.names.*;
import ideal.development.origins.*;

public class scanner_engine {
  private scanner_config config;

  public scanner_engine(scanner_config config) {
    this.config = config;
  }

  public readonly_list<token> scan(source_content source) {
    string input = source.content;
    list<token> tokens = new base_list<token>();
    for (int begin = 0; begin < input.size(); ) {
      char c = input.get(begin);

      if (config.is_whitespace(c)) {
        int end = begin + 1;
        for (; end < input.size(); ++end) {
          if (!config.is_whitespace(input.get(end))) {
            break;
          }
        }
        origin pos = source.make_origin(begin, end);
        string image = input.slice(begin, end);
        // TODO: handle newlines specially.
        tokens.append(new base_token<comment>(special_token_type.COMMENT,
            new comment(comment_type.WHITESPACE, image, image), pos));
        begin = end;
      } else if (config.is_name_start(c)) {
        int end = begin + 1;
        for (; end < input.size(); ++end) {
          if (!config.is_name_part(input.get(end))) {
            break;
          }
        }
        origin pos = source.make_origin(begin, end);
        string image = input.slice(begin, end);
        simple_name token_as_name = simple_name.make(image);
        token t = new base_token<simple_name>(special_token_type.SIMPLE_NAME, token_as_name, pos);
        t = config.process_token(t);
        tokens.append(t);
        begin = end;
      } else {
        scan_state next = null;
        readonly_list<scanner_element> elements = config.elements();
        for (int i = 0; i < elements.size(); ++i) {
          scanner_element element = elements.get(i);
          scan_state processed = element.process(source, begin);
          if (processed != null) {
            if (next == null) {
              next = processed;
            } else {
              int compare = processed.compare_to(next);
              assert compare != 0;
              if (compare > 0) {
                next = processed;
              }
            }
          }
        }

        if (next != null) {
          tokens.append(next.token);
          begin = next.end;
          continue;
        }

        int end = begin + 1;
        origin pos = source.make_origin(begin, end);
        new base_notification(messages.unrecognized_character, pos).report();
        begin = end;
      }
    }
    return tokens;
  }
}

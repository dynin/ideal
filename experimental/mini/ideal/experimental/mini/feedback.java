/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.experimental.mini;

import static ideal.experimental.mini.bootstrapped.*;
import static ideal.experimental.mini.library.*;

import javax.annotation.Nullable;

class feedback {

  public static boolean has_errors = false;

  public static void report(error_signal the_error_signal) {
    String message = the_error_signal.message().text();

    @Nullable origin deep_origin = the_error_signal.the_origin();
    while (deep_origin != null) {
      if (deep_origin instanceof text_position) {
        text_position position = (text_position) deep_origin;
        String content = position.the_origin().content();
        int index = position.character_index();
        int line_number = 1;
        for (int i = index - 1; i >= 0; --i) {
          if (content.charAt(i) == '\n') {
            line_number += 1;
          }
        }
        StringBuilder detailed = new StringBuilder();
        detailed.append(position.the_origin().name()).append(":");
        detailed.append(line_number).append(": ");
        detailed.append(message).append('\n');

        int start_of_line = index;
        while (start_of_line > 0 && content.charAt(start_of_line - 1) != '\n') {
          start_of_line -= 1;
        }
        int spaces;
        if (index >= content.length() || content.charAt(index) == '\n') {
          detailed.append(content.substring(start_of_line, index));
          detailed.append('\n');
        } else {
          int end_of_line = index;
          while (end_of_line < (content.length() - 1) && content.charAt(end_of_line + 1) != '\n') {
            end_of_line += 1;
          }
          detailed.append(content.substring(start_of_line, end_of_line + 1));
          detailed.append('\n');
        }
        for (int i = 0; i < (index - start_of_line); ++i) {
          detailed.append(' ');
        }
        detailed.append('^');
        // Last newline is added by println().
        message = detailed.toString();
        break;
      }
      if (deep_origin instanceof source_text) {
        message = ((source_text) deep_origin).name() + ": " + message;
        break;
      }
      if (deep_origin instanceof builtin_origin) {
        message = "<builtin>: " + message;
        break;
      }
      deep_origin = deep_origin.the_origin();
    }

    System.err.println(message);
    has_errors = true;
  }
}

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

import java.util.ArrayList;
import java.util.List;

public class tokenizer {

  public static List<token> tokenize(source_text the_source_text) {
    String content = the_source_text.content();
    int index = 0;
    List<token> result = new ArrayList<token>();
    while (index < content.length()) {
      int start = index;
      char prefix = content.charAt(index);
      index += 1;
      origin position = new text_position_class(the_source_text, start);
      if (is_identifier_start(prefix)) {
        while (index < content.length() && is_identifier_part(content.charAt(index))) {
          index += 1;
        }
        result.add(new identifier(content.substring(start, index), position));
      } else if (is_whitespace(prefix)) {
        while (index < content.length() && is_whitespace(content.charAt(index))) {
          index += 1;
        }
        result.add(new simple_token(core_token_type.WHITESPACE, position));
      } else if (prefix == '(') {
        result.add(new simple_token(punctuation.OPEN_PARENTHESIS, position));
      } else if (prefix == ')') {
        result.add(new simple_token(punctuation.CLOSE_PARENTHESIS, position));
      } else if (prefix == '.') {
        result.add(new operator(operator_type.DOT, position));
      } else if (prefix == '"') {
        char quote = prefix;
        while (index < content.length() &&
               content.charAt(index) != quote &&
               content.charAt(index) != '\n') {
          index += 1;
        }
        if (index == content.length()) {
          feedback.report(new error_signal(notification_type.EOF_IN_STRING_LITERAL, position));
        } else if (content.charAt(index) == '\n') {
          index += 1;
          feedback.report(new error_signal(notification_type.NEWLINE_IN_STRING_LITERAL, position));
        } else {
          assert content.charAt(index) == quote;
          String value = content.substring(start + 1, index);
          String with_quotes = content.substring(start, index + 1);
          index += 1;
          result.add(new string_literal(value, with_quotes, position));
        }
      } else if (prefix == ';') {
        while (index < content.length() && content.charAt(index) != '\n') {
          index += 1;
        }
        result.add(new simple_token(core_token_type.COMMENT, position));
      } else {
        feedback.report(new error_signal(notification_type.UNRECOGNIZED_CHARACTER, position));
      }
    }
    return result;
  }

  public static boolean is_identifier_start(char c) {
    return is_letter(c) || c == '_';
  }

  public static boolean is_identifier_part(char c) {
    return is_letter(c) || is_digit(c) || c == '_';
  }
}

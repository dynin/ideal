/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class string_literal extends debuggable implements literal<string> {

  private final string value;
  public final string with_escapes;
  public final quote_type quote;

  public string_literal(string value, string with_escapes, quote_type quote) {
    this.value = value;
    this.with_escapes = with_escapes;
    this.quote = quote;
  }

  // TODO: implement quoting framework.
  public string_literal(string the_value, quote_type quote) {
    this.value = the_value;
    this.with_escapes = escape_string_literal(the_value);
    this.quote = quote;
  }

  @Override
  public string the_value() {
    return value;
  }

  @Override
  public string to_string() {
    return with_escapes;
  }

  // TODO: this is a temporary workaround until a quoting framework is developed
  private static string escape_string_literal(string s) {
    StringBuilder result = new StringBuilder();
    String input = utilities.s(s);
    for (int i = 0; i < input.length(); ++i) {
      char the_character = input.charAt(i);
      boolean found = false;
      readonly_list<quoted_character> quoted_list = quoted_character.java_list;
      for (int quoted_index = 0; quoted_index < quoted_list.size(); quoted_index += 1) {
        quoted_character quoted = quoted_list.get(quoted_index);
        if (the_character == quoted.value_character) {
          result.append(quoted.with_escape());
          found = true;
          break;
        }
      }
      if (!found) {
        result.append(the_character);
      }
    }
    return new base_string(result.toString());
  }
}

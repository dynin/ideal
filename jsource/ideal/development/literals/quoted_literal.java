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
import ideal.development.elements.*;
import ideal.development.names.*;

public class quoted_literal extends debuggable implements literal<string> {

  private final string value;
  public final string with_escapes;
  public final quote_type quote;

  public quoted_literal(string value, string with_escapes, quote_type quote) {
    this.value = value;
    this.with_escapes = with_escapes;
    this.quote = quote;
  }

  // TODO: implement quoting framework.
  public quoted_literal(string the_value, quote_type quote) {
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
      char c = input.charAt(i);
      switch (c) {
        case '\\' :
          result.append("\\\\");
          break;
        case '\'' :
          result.append("\\'");
          break;
        case '\"' :
          result.append("\\\"");
          break;
        case '\n' :
          result.append("\\n");
          break;
        default:
          result.append(c);
          break;
      }
    }
    return new base_string(result.toString());
  }
}

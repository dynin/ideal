/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class quoted_literal extends debuggable implements literal<string> {

  private final string value;
  public final string with_escapes;
  public final token_type quote;

  public quoted_literal(string value, string with_escapes, token_type quote) {
    this.value = value;
    this.with_escapes = with_escapes;
    this.quote = quote;
  }

  // TODO: implement quoting framework.
  public quoted_literal(string the_value, token_type quote) {
   this(the_value, escape_string_literal(the_value), quote);
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
    return new base_string(utilities.s(s).
      replaceAll("\\\\", "\\\\").
      replaceAll("'", "\\\\'").
      replaceAll("\"", "\\\\\"").
      replaceAll("\n", "\\\\n"));
  }
}

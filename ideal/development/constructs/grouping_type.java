/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.punctuation;

public class grouping_type implements identifier, readonly_displayable  {
  public final string name;
  public final token_type start;
  public final token_type end;

  private grouping_type(String name, token_type start, token_type end) {
    this.name = new base_string(name);
    this.start = start;
    this.end = end;
  }

  @Override
  public string to_string() {
    return name;
  }

  @Override
  public string display() {
    return to_string();
  }

  public static grouping_type PARENS =
      new grouping_type("parens", punctuation.OPEN_PARENTHESIS, punctuation.CLOSE_PARENTHESIS);
  public static grouping_type BRACKETS =
      new grouping_type("brackets", punctuation.OPEN_BRACKET, punctuation.CLOSE_BRACKET);
  public static grouping_type ANGLE_BRACKETS =
      new grouping_type("angle_brackets", punctuation.LESS_THAN, punctuation.GREATER_THAN);
  public static grouping_type BRACES =
      new grouping_type("braces", punctuation.OPEN_BRACE, punctuation.CLOSE_BRACE);
}

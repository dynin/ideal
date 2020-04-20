/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.annotations.dont_display;

public class base_token_type implements token_type, readonly_displayable {
  private final string name;
  @dont_display
  private final int base_symbol;

  public base_token_type(String name, int base_symbol) {
    this.name = new base_string(name);
    this.base_symbol = base_symbol;
  }

  public base_token_type(String name) {
    this(name, -1);
  }

  @Override
  public string name() {
    return name;
  }

  @Override
  public boolean is_keyword() {
    return Character.isJavaIdentifierStart(name.first());
  }

  @Override
  public int symbol() {
    return base_symbol;
  }

  @Override
  public string to_string() {
    return new base_string("\"", name, "\"");
  }

  @Override
  public string display() {
    return name;
  }
}

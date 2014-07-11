/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;

public enum declaration_pass implements deeply_immutable_data, convertible_to_string,
    readonly_displayable {

  NONE,
  TYPES_AND_PROMOTIONS,
  METHODS_AND_VARIABLES;

  public boolean is_before(declaration_pass other) {
    return this.ordinal() < other.ordinal();
  }

  public boolean is_after(declaration_pass other) {
    return this.ordinal() > other.ordinal();
  }

  public static declaration_pass last() {
    return values()[values().length - 1];
  }

  @Override
  public string display() {
    return to_string();
  }

  @Override
  public string to_string() {
    return new base_string(name(), "/", String.valueOf(ordinal()));
  }
}

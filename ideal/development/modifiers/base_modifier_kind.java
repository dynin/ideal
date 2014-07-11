/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.modifiers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class base_modifier_kind extends debuggable implements modifier_kind,
    readonly_displayable {

  private final simple_name the_name;

  public base_modifier_kind(String name) {
    this.the_name = simple_name.make(name);
  }

  @Override
  public simple_name name() {
    return the_name;
  }

  @Override
  public string to_string() {
    return name_utilities.in_brackets(the_name);
  }

  @Override
  public string display() {
    return to_string();
  }
}

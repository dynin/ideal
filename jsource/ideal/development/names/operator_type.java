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

public class operator_type implements identifier, readonly_displayable {

  public final string name;
  public final int arity;

  private operator_type(String name, int arity) {
    this.name = new base_string(name);
    this.arity = arity;
  }

  @Override
  public string to_string() {
    return name;
  }

  @Override
  public string display() {
    return to_string();
  }

  public static operator_type PREFIX = new operator_type("prefix", 1);

  public static operator_type POSTFIX = new operator_type("postfix", 1);

  public static operator_type INFIX = new operator_type("infix", 2);

  // TODO: do we need this?
  public static operator_type ASSIGNMENT = new operator_type("assignment", 2);
}

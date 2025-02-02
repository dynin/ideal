/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

import javax.annotation.Nullable;

/**
 * Tuple for (type, action).
 */
class type_and_action extends debuggable implements immutable_data {
  private final type the_type;
  private final action the_action;

  public type_and_action(type the_type, action the_action) {
    assert the_type != null;
    assert the_action != null;

    this.the_type = the_type;
    this.the_action = the_action;
  }

  public type get_type() {
    return the_type;
  }

  public abstract_value result() {
    return the_action.result();
  }

  public action get_action() {
    return the_action;
  }

  @Override
  public string to_string() {
    return new base_string("(" + the_type + ", " + the_action + ")");
  }
}

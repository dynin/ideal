/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

import javax.annotation.Nullable;
public class action_plus_constraints extends debuggable implements analysis_result {
  public final action the_action;
  public final immutable_list<constraint> the_constraints;

  public action_plus_constraints(action the_action, immutable_list<constraint> the_constraints) {
    this.the_action = the_action;
    this.the_constraints = the_constraints;
  }

  public static analysis_result make_result(action the_action,
      readonly_list<constraint> the_constraints) {
    if (the_constraints.is_empty()) {
      return the_action;
    } else {
      return new action_plus_constraints(the_action, the_constraints.frozen_copy());
    }
  }

  @Override
  public origin deeper_origin() {
    return the_action;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_action);
  }
}

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;

public class base_analyzable_action extends debuggable implements analyzable_action {
  private final action the_action;

  public base_analyzable_action(action the_action) {
    if (the_action instanceof analyzable) {
      // TODO: may be do a static method that enforces this?
      utilities.panic("Don't wrap " + the_action);
    }
    this.the_action = the_action;
  }

  public static analyzable_action from(abstract_value value, origin the_origin) {
    return new base_analyzable_action(value.to_action(the_origin));
  }

  public static analyzable_action nothing(origin the_origin) {
    return from(common_library.get_instance().void_instance(), the_origin);
  }

  @Override
  public action get_action() {
    return the_action;
  }

  @Override
  public action analyze() {
    return get_action();
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    return this;
  }

  @Override
  public origin deeper_origin() {
    return get_action();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_action);
  }
}

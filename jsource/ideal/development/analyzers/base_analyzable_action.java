/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.values.*;

public class base_analyzable_action extends debuggable implements analyzable_action {
  private final action the_action;
  private final @Nullable analyzable child;

  public base_analyzable_action(action the_action, @Nullable analyzable child) {
    if (the_action instanceof analyzable) {
      // TODO: may be do a static method that enforces this?
      utilities.panic("Don't wrap " + the_action);
    }
    this.the_action = the_action;
    this.child = child;
  }

  public base_analyzable_action(action the_action) {
    this(the_action, null);
  }

  public static analyzable_action from(abstract_value value, origin the_origin) {
    return new base_analyzable_action(value.to_action(the_origin));
  }

  public static analyzable_action nothing(origin the_origin) {
    return new base_analyzable_action(common_values.nothing(the_origin));
  }

  @Override
  public action get_action() {
    return the_action;
  }

  @Override
  public boolean has_errors() {
    return the_action.result().type_bound() == common_types.error_type();
  }


  @Override
  public action analyze() {
    return get_action();
  }

  @Override
  public readonly_list<analyzable> children() {
    if (child != null) {
      return new base_list<analyzable>(child);
    } else {
      return new empty<analyzable>();
    }
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

/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

/**
 * A reference_wrapper implemented by the variable access.
 */
public class variable_wrapper extends debuggable implements reference_wrapper {

  private final variable_action the_action;
  private final variable_context the_context;

  protected variable_wrapper(variable_action the_action, variable_context the_context) {
    this.the_action = the_action;
    this.the_context = the_context;
  }

  @Override
  public type type_bound() {
    return the_action.type_bound();
  }

  @Override
  public type value_type_bound() {
    return the_action.value_type();
  }

  @Override
  public value_wrapper get() {
    value_wrapper val = the_context.get_var(the_action.the_declaration);
    if (val == null) {
      utilities.panic("Not defined: " + the_action.short_name() + " in " + the_context);
      // return common_library.do_get_undefined_instance();
    }
    return val;
  }

  @Override
  public void init(value_wrapper value) {
    // TODO: check that the value wasn't initialized or set before.
    the_context.put_var(the_action.the_declaration, value);
  }

  @Override
  public void set(value_wrapper value) {
    the_context.put_var(the_action.the_declaration, value);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_action.short_name());
  }
}

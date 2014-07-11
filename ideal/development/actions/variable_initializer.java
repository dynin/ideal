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
import ideal.development.notifications.*;
public class variable_initializer extends base_action {
  public final variable_action the_variable_action;
  public final action init;

  public variable_initializer(variable_action the_variable_action, action init) {
    super(the_variable_action.the_declaration);
    this.the_variable_action = the_variable_action;
    assert init != null;
    this.init = init;
  }

  @Override
  public abstract_value result() {
    return common_library.get_instance().void_type();
  }

  @Override
  public entity_wrapper execute(execution_context exec_context) {
    entity_wrapper init_value = init.execute(exec_context);
    if (init_value instanceof error_signal) {
      return init_value;
    }
    assert init_value instanceof value_wrapper;
    // TODO: add init method?
    the_variable_action.execute(exec_context).init((value_wrapper) init_value);

    return common_library.get_instance().void_instance();
  }
}

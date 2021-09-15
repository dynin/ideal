/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.types.*;

public class bound_procedure extends base_action implements stringable {
  public final action the_procedure_action;
  public final action_parameters parameters;
  public final abstract_value return_value;

  public bound_procedure(action the_procedure_action, abstract_value return_value,
      action_parameters parameters, origin source) {
    super(source);
    this.the_procedure_action = the_procedure_action;
    this.return_value = return_value;
    this.parameters = parameters;
  }

  public bound_procedure(procedure_value the_procedure_value, abstract_value return_value,
      action_parameters parameters, origin source) {
    this(the_procedure_value.to_action(source), return_value, parameters, source);
  }

  @Override
  public abstract_value result() {
    return return_value;
  }

  @Override
  @Nullable public declaration get_declaration() {
    return the_procedure_action.get_declaration();
  }

  @Override
  public action bind_from(action from, origin pos) {
    return new bound_procedure(the_procedure_action.bind_from(from, pos),
        return_value, parameters, pos);
  }

  @Override
  public boolean has_side_effects() {
    if (the_procedure_action.has_side_effects()) {
      return true;
    }

    declaration the_declaration = the_procedure_action.get_declaration();
    if (the_declaration instanceof procedure_declaration) {
      if (!((procedure_declaration) the_declaration).is_pure()) {
        return true;
      }
    }

    readonly_list<action> action_parameters = parameters.params();
    // TODO: use list.has()
    for (int i = 0; i < action_parameters.size(); ++i) {
      action parameter = action_parameters.get(i);
      if (parameter.has_side_effects()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity,
      execution_context the_execution_context) {
    entity_wrapper the_entity = the_procedure_action.execute(from_entity, the_execution_context);
    assert the_entity instanceof procedure_value;
    procedure_value the_procedure_value = (procedure_value) the_entity;
    assert !the_procedure_value.has_this_argument();

    readonly_list<action> action_parameters = parameters.params();
    list<entity_wrapper> concrete_values = new base_list<entity_wrapper>();

    for (int i = 0; i < action_parameters.size(); ++i) {
      action parameter = action_parameters.get(i);
      assert ! (parameter instanceof error_signal);
      entity_wrapper concrete_value = parameter.execute(null_wrapper.instance,
          the_execution_context);
      if (concrete_value instanceof jump_wrapper) {
        return concrete_value;
      }
      concrete_values.append(concrete_value);
    }

    return the_procedure_value.execute(concrete_values, the_execution_context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_procedure_action);
  }
}

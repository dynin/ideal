-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class bound_procedure {
  extends debuggable;
  implements action, stringable;

  private origin the_origin;
  action the_procedure_action;
  action_parameters parameters;
  abstract_value return_value;

  overload bound_procedure(action the_procedure_action, abstract_value return_value,
      action_parameters parameters, the origin) {
    -- TODO: redundant check
    assert the_origin is_not null;
    this.the_origin = the_origin;
    this.the_procedure_action = the_procedure_action;
    this.return_value = return_value;
    this.parameters = parameters;
  }

  overload bound_procedure(the procedure_value, abstract_value return_value,
      action_parameters parameters, the origin) {
    this(the_procedure_value.to_action(the_origin), return_value, parameters, the_origin);
  }

  override origin deeper_origin => the_origin;

  override abstract_value result => return_value;

  override declaration or null get_declaration => the_procedure_action.get_declaration;

  override boolean has_side_effects() {
    if (the_procedure_action.has_side_effects) {
      return true;
    }

    the_declaration : the_procedure_action.get_declaration;
    if (the_declaration is procedure_declaration) {
      if (!the_declaration.is_pure) {
        return true;
      }
    }

    -- TODO: use list.has()
    for (parameter : parameters.parameters) {
      if (parameter.has_side_effects) {
        return true;
      }
    }

    return false;
  }

  override action combine(action from, origin new_origin) =>
    bound_procedure.new(the_procedure_action.combine(from, new_origin), return_value,
        parameters, new_origin);

  override entity_wrapper execute(entity_wrapper from_entity, the execution_context) {
    the_entity : the_procedure_action.execute(from_entity, the_execution_context);
    if (the_entity is jump_wrapper) {
      return the_entity;
    }

    assert the_entity is procedure_value;
    the procedure_value : the_entity;

    concrete_values : base_list[entity_wrapper].new();

    -- TODO: use list.map()
    for (parameter : parameters.parameters) {
      assert parameter is_not error_signal;
      concrete_value : parameter.execute(null_wrapper.instance, the_execution_context);
      if (concrete_value is jump_wrapper) {
        return concrete_value;
      }
      concrete_values.append(concrete_value);
    }

    return the_procedure_value.execute(from_entity, concrete_values, the_execution_context);
  }

  override string to_string => utilities.describe(this, the_procedure_action);
}

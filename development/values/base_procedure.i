-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

abstract class base_procedure {
  extends base_data_value;
  implements procedure_value;

  private action_name the_action_name;
  private var procedure_declaration or null the_declaration;

  base_procedure(the action_name, type procedure_type) {
    super(procedure_type);
    this.the_action_name = the_action_name;
  }

  override action_name name => the_action_name;

  override boolean has_this_argument => false;

  override procedure_value bind_this(entity_wrapper this_argument) {
    if (has_this_argument) {
      return procedure_with_this.new(this, this_argument);
    } else {
      return this;
    }
  }

  override action bind_this_action(action from, the origin) {
    if (has_this_argument) {
      return procedure_with_this.new(this, from).to_action(the_origin);
    } else {
      return to_action(the_origin);
    }
  }

  set_declaration(procedure_declaration the_declaration) {
    assert this.the_declaration is null;
    this.the_declaration = the_declaration;
  }

  -- TODO redundant method
  override type type_bound => bound;

  override declaration or null get_declaration => the_declaration;

  protected var abstract_value return_value =>
    common_types.get_procedure_return(type_bound);

  protected boolean is_valid_procedure_arity(nonnegative arity) =>
    common_types.is_valid_procedure_arity(type_bound, arity);

  protected type get_argument_type(nonnegative index) =>
    common_types.get_procedure_argument(type_bound, index).type_bound;

  override boolean is_parametrizable => true;

  override boolean supports_parameters(action_parameters parameters, action_context context) {
    parameter_list : parameters.parameters;
    if (!is_valid_procedure_arity(parameter_list.size)) {
      return false;
    }

    for (index : parameter_list.indexes) {
      parameter : parameter_list[index];
      if (parameter is error_signal) {
        return false;
      }
      if (!context.can_promote(parameter, get_argument_type(index))) {
        return false;
      }
    }

    return true;
  }

  override analysis_result bind_parameters(the action_parameters, action_context context,
      the origin) {

    aparams : the_action_parameters.parameters;
    if (debug.DO_REDUNDANT_CHECKS) {
      -- This should never happen because of type checks done before bind_parameters is called
      if (!is_valid_procedure_arity(aparams.size)) {
        return error_signal.new("Arity mismatch", the_origin);
      }
    }

    promoted_params : base_list[action].new();

    for (index : aparams.indexes) {
      param : aparams[index];
      if (param is error_signal) {
        return param;
      }
      type_target : get_argument_type(index);
      if (context.can_promote(param, type_target)) {
        promoted_params.append(context.promote(param, type_target, the_origin));
      } else {
        return notification_utilities.cant_promote(param.result, type_target, the_origin);
      }
    }

    return bound_procedure.new(this, return_value, action_parameters.new(promoted_params),
        the_origin);
  }

  override abstract entity_wrapper execute(entity_wrapper this_argument,
      readonly list[entity_wrapper] args, the execution_context);

  override string to_string() {
    if (the_declaration is_not null) {
      return utilities.describe(this, the_declaration);
    } else {
      return utilities.describe(this, name);
    }
  }
}

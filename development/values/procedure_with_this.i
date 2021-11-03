-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class procedure_with_this {
  extends base_data_value;
  implements procedure_value;

  private procedure_value the_procedure;
  entity_wrapper or null this_entity;
  action or null this_action;

  overload procedure_with_this(procedure_value the_procedure, action this_action) {
    super(the_procedure.type_bound);
    this.the_procedure = the_procedure;
    this.this_entity = missing.instance;
    assert this_action is_not null;
    this.this_action = this_action;
    assert the_procedure.has_this_argument;
  }

  overload procedure_with_this(procedure_value the_procedure, entity_wrapper this_entity) {
    super(the_procedure.type_bound);
    this.the_procedure = the_procedure;
    assert this_entity is_not null;
    this.this_entity = this_entity;
    this.this_action = missing.instance;
    assert the_procedure.has_this_argument;
  }

  override action_name name => the_procedure.name;

  override declaration or null get_declaration => the_procedure.get_declaration;

  override boolean has_this_argument => false;

  override procedure_value bind_this(entity_wrapper this_argument) =>
      procedure_with_this.new(this, this_argument);

  override action bind_this_action(action from, the origin) {
    -- TODO: handle this_action set to null
    assert this_action is_not null;
    return procedure_with_this.new(the_procedure, this_action.combine(from, the_origin)).
        to_action(the_origin);
  }

  override boolean supports_parameters(action_parameters parameters, action_context context) =>
      the_procedure.supports_parameters(parameters, context);

  override analysis_result bind_parameters(action_parameters params, action_context context,
      the origin) {
    bound_procedure : the_procedure.bind_parameters(params, context, the_origin);
    if (bound_procedure is error_signal) {
      return bound_procedure;
    }

    -- TODO: what if this is action_plus_constraints?
    assert bound_procedure is action;
    -- TODO: handle this_action set to null
    assert this_action is_not null;
    return bound_procedure.combine(this_action, the_origin);
  }

  override entity_wrapper execute(entity_wrapper from_entity,
      readonly list[entity_wrapper] arguments, the execution_context) {
    assert from_entity is_not null;
    var entity_wrapper this_value;

    if (this_entity is_not null) {
      this_value = this_entity;
    } else {
      if (this_action is_not null) {
        this_value = this_action.execute(null_wrapper.instance, the_execution_context);
      } else {
        this_value = from_entity;
      }
    }

    if (this_value is jump_wrapper) {
      return this_value;
    }

    return the_procedure.execute(this_value, arguments, the_execution_context);
  }

  override string to_string => utilities.describe(this, the_procedure);
}

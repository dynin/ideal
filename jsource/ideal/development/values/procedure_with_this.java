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
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.jumps.*;

public class procedure_with_this extends base_data_value<procedure_value>
    implements procedure_value {

  private final procedure_value the_procedure;
  public final entity_wrapper this_entity;
  public final action this_action;

  public procedure_with_this(procedure_value the_procedure, action this_action) {
    super(the_procedure.type_bound());
    this.the_procedure = the_procedure;
    this.this_entity = null;
    assert this_action != null;
    this.this_action = this_action;
    assert the_procedure.has_this_argument();
  }

  public procedure_with_this(procedure_value the_procedure, entity_wrapper this_entity) {
    super(the_procedure.type_bound());
    this.the_procedure = the_procedure;
    assert this_entity != null;
    this.this_entity = this_entity;
    this.this_action = null;
    assert the_procedure.has_this_argument();
  }

  @Override
  public action_name name() {
    return the_procedure.name();
  }

  @Override
  public declaration get_declaration() {
    return the_procedure.get_declaration();
  }

  @Override
  public boolean has_this_argument() {
    return false;
  }

  @Override
  public procedure_value bind_this(entity_wrapper this_argument) {
    return new procedure_with_this(this, this_argument);
  }

  @Override
  public action bind_value(action from, origin the_origin) {
    return new procedure_with_this(the_procedure, this_action.combine(from, the_origin)).
        to_action(the_origin);
  }

  @Override
  public boolean supports_parameters(action_parameters parameters, action_context context) {
    return the_procedure.supports_parameters(parameters, context);
  }

  @Override
  public analysis_result bind_parameters(action_parameters params, action_context context,
      origin the_origin) {
    analysis_result bound_procedure = the_procedure.bind_parameters(params, context, the_origin);
    if (bound_procedure instanceof error_signal) {
      return (error_signal) bound_procedure;
    }

    // TODO: what if this is action_plus_constraints?
    assert bound_procedure instanceof action;
    return ((action) bound_procedure).combine(this_action, the_origin);
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, readonly_list<entity_wrapper> arguments,
      execution_context the_execution_context) {
    assert from_entity != null;
    entity_wrapper this_value;

    if (this_entity != null) {
      this_value = this_entity;
    } else {
      if (from_entity instanceof null_wrapper) {
        this_value = this_action.execute(null_wrapper.instance, the_execution_context);
      } else {
        this_value = from_entity;
      }
    }

    if (this_value instanceof jump_wrapper) {
      return this_value;
    }

    return the_procedure.execute(this_value, arguments, the_execution_context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_procedure);
  }
}

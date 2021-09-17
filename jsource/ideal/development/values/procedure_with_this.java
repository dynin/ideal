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
import ideal.development.types.*;
import ideal.development.flavors.*;

public class procedure_with_this extends base_data_value<procedure_value>
    implements procedure_value {

  private final procedure_value the_procedure;
  public final action this_action;

  public procedure_with_this(procedure_value the_procedure, action this_action) {
    super(the_procedure.type_bound());
    this.the_procedure = the_procedure;
    this.this_action = this_action;
    assert the_procedure.has_this_argument();
    assert this_action != null;
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
    return new procedure_with_this(this, new entity_action(this_argument));
  }

  @Override
  public base_data_value bind_value(action from, origin pos) {
    return new procedure_with_this(the_procedure, action_utilities.combine(from, this_action, pos));
  }

  @Override
  public boolean supports_parameters(action_parameters parameters, analysis_context context) {
    return the_procedure.supports_parameters(parameters, context);
  }

  @Override
  public analysis_result bind_parameters(action_parameters params, analysis_context context,
      origin pos) {
    analysis_result bound_procedure = the_procedure.bind_parameters(params, context, pos);
    if (bound_procedure instanceof error_signal) {
      return (error_signal) bound_procedure;
    } else {
      return action_utilities.combine(this_action, ((action) bound_procedure), pos);
    }
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, readonly_list<entity_wrapper> arguments,
      execution_context the_execution_context) {
    entity_wrapper this_value;

    if (from_entity instanceof null_wrapper) {
      this_value = this_action.execute(null_wrapper.instance, the_execution_context);
    } else {
      this_value = from_entity;
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

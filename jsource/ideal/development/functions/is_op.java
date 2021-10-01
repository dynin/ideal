/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.functions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.declarations.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

public class is_op extends binary_procedure {

  public final boolean negated;

  // TODO: this should use any flavor, not readonly.
  public is_op(operator the_operator, boolean negated) {
    super(the_operator, true,
        library().immutable_boolean_type(),
        library().value_type().get_flavored(flavor.any_flavor),
        elementary_types.any_type());
    this.negated = negated;
  }

  @Override
  protected analysis_result bind_binary(action first, action second, analysis_context context,
      origin pos) {
    action first_value = analyzer_utilities.to_value(first, context, pos);

    if (first_value instanceof error_signal) {
      return (error_signal) first_value;
    }

    if (! (second instanceof type_action)) {
      return new error_signal(messages.type_expected, pos);
    }
    type the_type = analyzer_utilities.handle_default_flavor(second.result());

    // TODO: check that first_value.result() is a subtype of the_type...

    action the_action = new is_action(first_value, the_type, negated, pos);

    if (analyzer_utilities.supports_constraint(first)) {
      variable_declaration the_declaration =
          (variable_declaration) declaration_util.get_declaration(first);
      list<constraint> constraints = new base_list<constraint>();
      constraints.append(new constraint(the_declaration, the_type,
          negated ? constraint_type.ON_FALSE : constraint_type.ON_TRUE));
      @Nullable abstract_value other =
          get_other_type(first_value.result().type_bound(), the_type);
      if (other != null) {
        constraints.append(new constraint(the_declaration, other,
            negated ? constraint_type.ON_TRUE : constraint_type.ON_FALSE));
      }
      return action_plus_constraints.make_result(the_action, constraints);
    }

    return the_action;
  }

  private static @Nullable abstract_value get_other_type(type action_type, type is_type) {
    if (type_utilities.is_union(action_type)) {
      immutable_list<abstract_value> parameters = type_utilities.get_union_parameters(action_type);
      // TODO: in the future, we'll support union types with more than two parameters...
      assert parameters.size() == 2;
      if (parameters.get(0) == is_type) {
        return parameters.get(1);
      } else if (parameters.get(1) == is_type) {
        return parameters.get(0);
      }
    }
    return null;
  }


  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context) {

    utilities.panic("Unimplemented is_op.execute_binary()");
    return null;
  }
}

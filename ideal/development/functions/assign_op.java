/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

public class assign_op extends binary_procedure {

  // TODO: this should use any flavor, not readonly.
  public assign_op() {
    super(operator.ASSIGN, false,
        library().value_type().get_flavored(flavors.any_flavor),
        library().entity_type().get_flavored(flavors.any_flavor),
        library().value_type().get_flavored(flavors.any_flavor));
  }

  @Override
  protected analysis_result bind_binary(action first, action second, analysis_context context,
      position pos) {
    list<constraint> constraints = new base_list<constraint>();

    if (first instanceof narrow_action) {
      narrow_action narrowed_variable = (narrow_action) first;
      constraints.append(new constraint(narrowed_variable.the_declaration,
          narrowed_variable.the_declaration.reference_type(), constraint_type.ALWAYS));
      first = narrowed_variable.expression;
    }

    type reference_type = first.result().type_bound();
    if (!library().is_reference_type(reference_type)) {
      // TODO: check that this is a writable reference.
      return new error_signal(new base_string("Reference expected, got ",
          context.print_value(reference_type)), pos);
    }

    type value_type = library().get_reference_parameter(reference_type);

    type writable_ref = library().get_reference(flavors.writeonly_flavor, value_type);
    if (!context.can_promote(first.result(), writable_ref)) {
      return new error_signal(new base_string("Writable reference expected, got ",
          context.print_value(reference_type)), pos);
    }
    action left = context.promote(first, writable_ref, pos);

    // TODO: this is used in base_string.i; remove once string handling is improved.
    if (jinterop_library.is_enabled() &&
        value_type == immutable_java_string() &&
        !context.can_promote(second.result(), immutable_java_string())) {
      value_type = library().immutable_string_type();
    }

    if (!context.can_promote(second.result(), value_type)) {
      return action_utilities.cant_promote(second.result(), value_type, context, pos);
    }
    action the_value = context.promote(second, value_type, pos);

    return action_plus_constraints.make_result(make_action(value_type, left, the_value, pos),
        constraints);
  }

  private type immutable_java_string() {
    return jinterop_library.get_instance().string_type().
        get_flavored(flavors.deeply_immutable_flavor);
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context) {

    assert first instanceof reference_wrapper;
    assert second instanceof value_wrapper;

    ((reference_wrapper) first).set((value_wrapper) second);

    return second;
  }
}

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.development.transformers.*;

public class assign_op extends binary_procedure {

  // TODO: this should use any flavor, not readonly.
  public assign_op() {
    super(operator.ASSIGN, false,
        library().value_type().get_flavored(flavor.any_flavor),
        library().entity_type().get_flavored(flavor.any_flavor),
        library().value_type().get_flavored(flavor.any_flavor));
  }

  @Override
  protected analysis_result bind_binary(action first, action second, analysis_context context,
      origin pos) {

    boolean first_narrowed = first instanceof narrow_action;
    narrow_action narrowed_variable = first_narrowed ? (narrow_action) first : null;

    if (first_narrowed) {
      first = narrowed_variable.expression;
    }

    type reference_type = first.result().type_bound();
    if (!library().is_reference_type(reference_type)) {
      // TODO: check that this is a writable reference.
      return new error_signal(new base_string("Reference expected, got ",
          context.print_value(reference_type)), pos);
    }

    type value_type = library().get_reference_parameter(reference_type);

    type writable_ref = library().get_reference(flavor.writeonly_flavor, value_type);
    if (!context.can_promote(first, writable_ref)) {
      return new error_signal(new base_string("Writable reference expected, got ",
          context.print_value(reference_type)), pos);
    }
    action left = context.promote(first, writable_ref, pos);

    // TODO: this is used in base_string.i; remove once string handling is improved.
    if (java_library.is_java_type(value_type)) {
      // This causes loading of java adapter, so the check above avoids it unless necessary
      type java_string = java_library.get_instance().string_type().
          get_flavored(flavor.deeply_immutable_flavor);
      if (value_type == java_string && !context.can_promote(second, java_string)) {
        value_type = library().immutable_string_type();
      }
    }

    if (!context.can_promote(second, value_type)) {
      return action_utilities.cant_promote(second.result(), value_type, context, pos);
    }
    action the_value = context.promote(second, value_type, pos);

    list<constraint> constraints = new base_list<constraint>();
    if (first_narrowed) {
      constraints.append(new constraint(narrowed_variable.the_declaration, second.result(),
          constraint_type.ALWAYS));
    }

    return action_plus_constraints.make_result(make_action(value_type, left, the_value, pos),
        constraints);
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

/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.extensions.*;

public class cast_op extends binary_procedure {

  // TODO: this should use any flavor, not readonly.
  public cast_op(cast_type the_cast_type) {
    super(the_cast_type, true,
        common_types.value_type().get_flavored(flavor.any_flavor),
        common_types.value_type().get_flavored(flavor.any_flavor),
        common_types.any_type());
  }

  public cast_type the_cast_type() {
    return (cast_type) name();
  }

  public boolean is_soft_cast() {
    return the_cast_type() == operator.SOFT_CAST;
  }

  @Override
  protected analysis_result bind_binary(action first, action second, action_context context,
      origin pos) {
    first = analyzer_utilities.to_value(first, context, pos);
    if (first instanceof error_signal) {
      return first;
    }

    while (second instanceof extension_action) {
      second = ((extension_action) second).extended_action;
    }

    if (! (second instanceof type_action)) {
      return new error_signal(messages.type_expected, second);
    }

    type the_type = analyzer_utilities.handle_default_flavor(second.result());

    if (!the_type.is_subtype_of(common_types.value_type().get_flavored(flavor.any_flavor))) {
      return new error_signal(new base_string("Expected value subtype, got " + the_type), pos);
    }

    if (is_soft_cast()) {
      if (!context.can_promote(first, the_type)) {
        return new error_signal(new base_string("Can't promote " + first.result() +
            " in soft cast to " + the_type), pos);
      }
      first = context.promote(first, the_type, pos);
    }

    // TODO: check that expression.result() is a subtype of the_type...
    return new cast_action(first, the_type, the_cast_type(), pos);
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context) {

    utilities.panic("Unimplemented cast_op.execute_binary()");
    return null;
  }
}

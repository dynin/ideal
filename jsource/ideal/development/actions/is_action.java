/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;

public class is_action extends base_action {
  public final action expression;
  public final type the_type;
  public final boolean negated;

  public is_action(action expression, type the_type, boolean negated, origin source) {
    super(source);
    this.expression = expression;
    this.the_type = the_type;
    this.negated = negated;
  }

  public operator get_operator() {
    return negated ? operator.IS_NOT_OPERATOR : operator.IS_OPERATOR;
  }

  @Override
  public abstract_value result() {
    return common_library.get_instance().immutable_boolean_type();
  }

  @Override
  public boolean has_side_effects() {
    return expression.has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context the_context) {
    // Handle jumps
    entity_wrapper expression_result = expression.execute(null_wrapper.instance, the_context);
    assert expression_result instanceof value_wrapper;

    boolean result = action_utilities.is_of(expression_result, the_type);
    if (negated) {
      result = !result;
    }
    return common_library.get_instance().to_boolean_value(result);
  }

  @Override
  public string to_string() {
    return new base_string(expression + (negated ? " is_not " : " is ") + the_type);
  }
}

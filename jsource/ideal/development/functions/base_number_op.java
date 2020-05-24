/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.functions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
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

public abstract class base_number_op extends binary_procedure {

  public base_number_op(operator the_operator, type return_type) {
    super(the_operator, true, return_type, library().immutable_integer_type(),
        library().immutable_integer_type());
  }

  private action promote_to_integer(action argument, analysis_context context, origin pos) {
    abstract_value argument_value = argument.result();
    if (context.can_promote(argument_value, library().immutable_nonnegative_type())) {
      return context.promote(argument, library().immutable_nonnegative_type(), pos);
    } else {
      return context.promote(argument, library().immutable_integer_type(), pos);
    }
  }

  @Override
  protected action bind_binary(action first, action second, analysis_context context,
      origin pos) {
    boolean all_nonnegative = true;

    first = promote_to_integer(first, context, pos);
    all_nonnegative &= (first.result() == library().immutable_nonnegative_type());
    second = promote_to_integer(second, context, pos);
    all_nonnegative &= (second.result() == library().immutable_nonnegative_type());

    type result;
    if (return_value() == library().immutable_boolean_type()) {
      result = library().immutable_boolean_type();
    } else {
      result = all_nonnegative ? library().immutable_nonnegative_type() :
          library().immutable_integer_type();
    }
    return make_action(result, first, second, pos);
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
          execution_context the_execution_context) {

    int first_value;
    int second_value;

    if (first instanceof integer_value) {
      first_value = ((integer_value) first).unwrap();
    } else {
      utilities.panic("Non-integer first argument to plus: " + first);
      return null;
    }

    if (second instanceof integer_value) {
      second_value = ((integer_value) second).unwrap();
    } else {
      utilities.panic("Non-integer second argument to plus: " + second);
      return null;
    }

    Object result = apply(first_value, second_value);

    if (result instanceof Integer) {
      int integer_result = (Integer) result;
      return new integer_value(integer_result,
          (integer_result > 0) ? library().immutable_nonnegative_type() :
              library().immutable_integer_type());
    } else {
      boolean boolean_result = (Boolean) result;
      return boolean_result ? library().true_value() : library().false_value();
    }
  }

  protected abstract Object apply(int first, int second);
}

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

public abstract class binary_procedure extends base_procedure {

  // TODO: also have a procedure here.
  public binary_procedure(action_name name, boolean is_function,
      type return_type, type first_argument_type,
      type second_argument_type) {
    super(name, procedure_util.make_procedure_type(is_function, return_type, first_argument_type,
        second_argument_type));

    assert action_utilities.is_valid_procedure_arity(type_bound(), 2);
  }

  @Override
  public final entity_wrapper execute(readonly_list<entity_wrapper> args,
          execution_context exec_context) {
    assert args.size() == 2;
    return execute_binary(args.get(0), args.get(1), exec_context);
  }

  @Override
  public final analysis_result bind_parameters(action_parameters params, analysis_context context,
      origin pos) {

    // TODO: unify this with base_procedure.bind_parameters()
    readonly_list<action> aparams = params.params();
    if (!action_utilities.is_valid_procedure_arity(type_bound(), aparams.size())) {
      return new error_signal(new base_string("Arity mismatch"), pos);
    }

    for (int i = 0; i < aparams.size(); ++i) {
      action param = aparams.get(i);
      if (param instanceof error_signal) {
        return param;
      }
      type target = get_argument_type(i);
      if (!context.can_promote(param.result(), target)) {
        return new error_signal(new base_string("Promotion error"), pos);
      }
    }

    return do_bind_parameters(aparams, context, pos);
  }

  protected analysis_result do_bind_parameters(readonly_list<action> aparams,
      analysis_context context, origin pos) {
    return bind_binary(aparams.get(0), aparams.get(1), context, pos);
  }

  protected analysis_result bind_binary(action first, action second, analysis_context context,
      origin pos) {
    first = context.promote(first, get_argument_type(0), pos);
    second = context.promote(second, get_argument_type(1), pos);

    return make_action(return_value(), first, second, pos);
  }

  protected final action make_action(abstract_value return_value, action first, action second,
      origin pos) {
    list<action> new_params = new base_list<action>();
    new_params.append(first);
    new_params.append(second);
    return new bound_procedure(this, return_value, new action_parameters(new_params), pos);
  }

  protected abstract entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context);
}

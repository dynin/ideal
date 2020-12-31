/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class list_initializer_analyzer extends single_pass_analyzer {

  public final readonly_list<analyzable> analyzable_parameters;
  public type element_type;
  public list<action> parameter_actions;

  public list_initializer_analyzer(list_construct source) {
    super(source);
    assert !source.is_simple_grouping();
    analyzable_parameters = make_list(source.elements);
  }

  public type element_type() {
    return element_type;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    parameter_actions = new base_list<action>();
    error_signal error = null;
    action result_action = null;

    for (int i = 0; i < analyzable_parameters.size(); ++i) {
      analyzable param = analyzable_parameters.get(i);
      @Nullable error_signal arg_error = find_error(param);
      if (arg_error != null) {
        if (error == null) {
          error = arg_error;
        }
        continue;
      }
      action the_action = analyzer_utilities.to_value(action_not_error(param), get_context(), this);
      if (result_action == null) {
        result_action = the_action;
      } else {
        if (get_context().can_promote(the_action, result_action.result().type_bound())) {
          // Keep result action
        } else if (get_context().can_promote(result_action, the_action.result().type_bound())) {
          result_action = the_action;
        } else {
          error_signal unify_error = new error_signal(
              new base_string("Can't figure out element type"), this);
          maybe_report_error(unify_error);
          if (error == null) {
            error = unify_error;
          }
        }
      }
      parameter_actions.append(the_action);
    }

    if (error != null) {
      return new error_signal(messages.error_in_list_initilizer, error, this);
    }

    // TODO: handle empty list
    assert result_action != null;
    element_type = result_action.result().type_bound();

    for (int i = 0; i < parameter_actions.size(); ++i) {
      parameter_actions.at(i).set(get_context().promote(parameter_actions.get(i),
          element_type, this));
    }

    return new list_initializer_action(element_type, parameter_actions, this);
  }

/*
  TODO: implement specializer
  @Override
  public list_initializer_analyzer specialize(specialization_context context,
      principal_type new_parent) {
  */

  @Override
  public string to_string() {
    return utilities.describe(this);
  }
}

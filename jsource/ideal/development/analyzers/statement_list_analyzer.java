/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;

/**
 * Analyze a sequence (list) of actions.  Unlike |block_analyzer|, no frame is created.
 */
public class statement_list_analyzer extends single_pass_analyzer {
  private readonly_list<analyzable> the_elements;

  public statement_list_analyzer(readonly_list<analyzable> the_elements, origin pos) {
    super(pos);
    this.the_elements = the_elements;
  }

  public statement_list_analyzer(origin pos) {
    super(pos);
    the_elements = null;
  }

  public void populate(readonly_list<analyzable> the_elements) {
    assert this.the_elements == null;
    assert the_elements != null;
    this.the_elements = the_elements;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    list<action> actions = new base_list<action>();
    analysis_context current_context = get_context();
    error_signal error = null;

    // TODO: should we stop on first error here?
    for (int i = 0; i < the_elements.size(); ++i) {
      analyzable the_element = the_elements.get(i);
      special_init_context(the_element, current_context);
      analysis_result the_result = the_element.analyze();

      action the_action;

      if (the_result instanceof action) {
        the_action = (action) the_result;
      } else if (the_result instanceof error_signal) {
        if (error == null) {
          error = new error_signal(messages.error_in_list, the_element, this);
        }
        continue;
      } else if (the_result instanceof action_plus_constraints) {
        the_action = ((action_plus_constraints) the_result).the_action;
        immutable_list<constraint> the_constraints =
            ((action_plus_constraints) the_result).the_constraints;

        // TODO: use list.filter()
        list<constraint> always_constraints = new base_list<constraint>();
        for (int j = 0; j < the_constraints.size(); ++j) {
          constraint the_constraint = the_constraints.get(j);
          if (the_constraint.the_constraint_type == constraint_type.ALWAYS) {
            always_constraints.append(the_constraint);
          }
        }
        current_context = constrained_analysis_context.combine(current_context, always_constraints);
      } else {
        utilities.panic("Unexpected result: " + the_result);
        return null;
      }

      actions.append(the_action);
      if (the_action.result() == core_types.unreachable_type() && i > (the_elements.size() - 1)) {
        if (error == null) {
          error = new error_signal(new base_string("Unreachable code"), the_elements.get(i + 1));
        }
      }
    }

    if (error != null) {
      return error;
    } else {
      return new list_action(actions, this);
    }
  }
}

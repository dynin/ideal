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
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.names.*;
import ideal.development.types.*;

/**
 * Analyze a sequence (list) of actions.  Unlike |block_analyzer|, no frame is created.
 */
public class statement_list_analyzer extends multi_pass_analyzer {
  private @Nullable readonly_list<analyzable> the_elements;
  private boolean declaration_list;
  private analysis_result result;

  public statement_list_analyzer(@Nullable readonly_list<analyzable> the_elements,
      boolean declaration_list, origin the_origin) {
    super(the_origin);
    this.the_elements = the_elements;
    this.declaration_list = declaration_list;
  }

  public statement_list_analyzer(readonly_list<analyzable> the_elements, origin the_origin) {
    this(the_elements, false, the_origin);
  }

  public statement_list_analyzer(origin the_origin) {
    this(null, false, the_origin);
  }

  public statement_list_analyzer(readonly_list<construct> constructs, principal_type parent,
      analysis_context context, origin the_origin) {
    super(the_origin, parent, context);
    assert constructs != null;
    the_elements = make_list(constructs);
    declaration_list = true;
  }

  protected statement_list_analyzer(readonly_list<analyzable> the_elements,
      boolean declaration_list, principal_type parent, analysis_context context,
      origin the_origin) {
    super(the_origin, parent, context);
    this.the_elements = the_elements;
    this.declaration_list = declaration_list;
  }

  public void populate(readonly_list<analyzable> the_elements) {
    assert this.the_elements == null;
    assert the_elements != null;
    this.the_elements = the_elements;
  }

  public readonly_list<analyzable> elements() {
    return the_elements;
  }

  @Override
  public readonly_list<analyzable> children() {
    return the_elements;
  }

  public readonly_list<declaration> declarations() {
    // TODO: use list.filter()
    list<declaration> result = new base_list<declaration>();
    for (int i = 0; i < the_elements.size(); ++i) {
      analyzable element = the_elements.get(i);
      if (element instanceof declaration) {
        result.append((declaration) element);
      }
    }
    return result;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    if (declaration_list) {
      signal declaration_signal = declaration_analysis(pass);
      if (declaration_signal instanceof error_signal) {
        return declaration_signal;
      }
    } else {
      if (pass == analysis_pass.BODY_CHECK) {
        return sequence_analysis();
      }
    }

    return ok_signal.instance;
  }

  protected signal sequence_analysis() {
    list<action> actions = new base_list<action>();
    analysis_context current_context = get_context();
    list<constraint> local_end_constraints = new base_list<constraint>();
    list<constraint> end_constraints = new base_list<constraint>();
    error_signal error = null;

    // TODO: should we stop on first error here?
    for (int i = 0; i < the_elements.size(); ++i) {
      analyzable the_element = the_elements.get(i);
      special_init_context(the_element, current_context);
      analysis_result the_result = the_element.analyze();

      action the_action;

      if (the_result instanceof action) {
        the_action = (action) the_result;
        if (the_action.has_side_effects()) {
          current_context = constrained_analysis_context.clear_non_local(current_context);
          end_constraints.clear();
        }
      } else if (the_result instanceof error_signal) {
        if (error == null) {
          error = new error_signal(messages.error_in_list, the_element, this);
        }
        continue;
      } else if (the_result instanceof action_plus_constraints) {
        the_action = ((action_plus_constraints) the_result).the_action;
        immutable_list<constraint> the_constraints =
            ((action_plus_constraints) the_result).the_constraints;
        if (the_action.has_side_effects()) {
          current_context = constrained_analysis_context.clear_non_local(current_context);
          end_constraints.clear();
        }

        // TODO: use list.filter()
        list<constraint> always_constraints = new base_list<constraint>();
        for (int j = 0; j < the_constraints.size(); ++j) {
          constraint the_constraint = the_constraints.get(j);
          if (the_constraint.the_constraint_type == constraint_type.ALWAYS) {
            always_constraints.append(the_constraint);
            if (the_constraint.is_local()) {
              local_end_constraints.append(the_constraint);
            } else {
              end_constraints.append(the_constraint);
            }
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
    }

    list_action the_list_action = new list_action(actions, this);
    end_constraints.append_all(local_end_constraints);
    if (end_constraints.is_empty()) {
      result = the_list_action;
    } else {
      result = action_plus_constraints.make_result(the_list_action, end_constraints);
    }

    return ok_signal.instance;
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    if (has_errors()) {
      return this;
    }

    list<analyzable> new_elements = new base_list<analyzable>();
    boolean same = true;
    for (int i = 0; i < the_elements.size(); ++i) {
      analyzable element = the_elements.get(i);
      analyzable new_element = element.specialize(context, new_parent);
      if (new_element != element) {
        same = false;
      }
      new_elements.append(new_element);
    }

    if (same) {
      return this;
    } else {
      return new statement_list_analyzer(new_elements, declaration_list, new_parent, get_context(),
          this);
    }
  }

  protected signal declaration_analysis(analysis_pass pass) {
    boolean is_last_pass = pass == analysis_pass.BODY_CHECK;
    error_signal error = null;
    list<action> actions = new base_list<action>();

    for (int i = 0; i < the_elements.size(); ++i) {
      analyzable element = the_elements.get(i);
      signal element_result = analyze(element, pass);
      if (element_result instanceof error_signal) {
        if (error == null) {
          error = new error_signal(messages.error_in_list, element, this);
        }
      } else if (is_last_pass) {
        actions.append(action_not_error(element));
      }
    }

    if (is_last_pass) {
      result = new list_action(actions, this);
    }

    return error != null ? error : ok_signal.instance;
  }

  @Override
  protected analysis_result do_get_result() {
    assert result != null;
    return result;
  }
}

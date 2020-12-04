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
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.notifications.*;
import ideal.development.flavors.*;

public class conditional_analyzer extends single_pass_analyzer {

  public final analyzable condition;
  public final analyzable then_branch;
  public final @Nullable analyzable else_branch;

  public conditional_analyzer(conditional_construct source) {
    super(source);
    condition = make(source.cond_expr);
    then_branch = make(source.then_expr);
    else_branch = source.else_expr != null ? make(source.else_expr) : null;
  }

  public conditional_analyzer(analyzable condition, analyzable then_branch,
      @Nullable analyzable else_branch, origin pos) {
    super(pos);
    this.condition = condition;
    this.then_branch = then_branch;
    this.else_branch = else_branch;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    origin the_origin = this;
    if (has_analysis_errors(condition)) {
      return new error_signal(messages.error_in_conditional, condition, the_origin);
    }

    analysis_result condition_result = condition.analyze();
    action condition_action;
    immutable_list<constraint> new_constraints;
    if (condition_result instanceof action) {
      condition_action = (action) condition_result;
      new_constraints = new empty<constraint>();
    } else {
      action_plus_constraints the_result = (action_plus_constraints) condition_result;
      condition_action = the_result.the_action;
      new_constraints = the_result.the_constraints;
    }

    type boolean_type = library().immutable_boolean_type();
    if (!get_context().can_promote(condition_action, boolean_type)) {
      return new error_signal(new base_string("Boolean value expected, got " +
          condition_action.result()), condition_action);
    }
    condition_action = get_context().promote(condition_action, boolean_type, the_origin);
    // TODO: this check is redundant.
    assert !(condition_action instanceof error_signal);

    analysis_result analyzed_then = analyze_with_constraints(then_branch, new_constraints,
        constraint_type.ON_TRUE);
    if (analyzed_then instanceof error_signal) {
      return new error_signal(messages.error_in_conditional, (error_signal) analyzed_then,
          the_origin);
    }
    action then_action;
    immutable_list<constraint> then_constraints;
    if (analyzed_then instanceof action) {
      then_action = (action) analyzed_then;
      then_constraints = new empty<constraint>();
    } else {
      action_plus_constraints then_result = (action_plus_constraints) analyzed_then;
      then_action = then_result.the_action;
      then_constraints = then_result.the_constraints;
    }

    action else_action;
    immutable_list<constraint> else_constraints = new empty<constraint>();
    if (else_branch != null) {
      analysis_result analyzed_else = analyze_with_constraints(else_branch, new_constraints,
          constraint_type.ON_FALSE);
      if (analyzed_else instanceof error_signal) {
        return new error_signal(messages.error_in_conditional, (error_signal) analyzed_else,
            the_origin);
      }
      if (analyzed_else instanceof action) {
        else_action = (action) analyzed_else;
      } else {
        action_plus_constraints else_result = (action_plus_constraints) analyzed_else;
        else_action = else_result.the_action;
        else_constraints = else_result.the_constraints;
      }
    } else {
      else_action = library().void_instance().to_action(the_origin);
    }

    then_action = analyzer_utilities.to_value(then_action, get_context(), the_origin);
    else_action = analyzer_utilities.to_value(else_action, get_context(), the_origin);

    @Nullable type result_type = analyzer_utilities.unify(then_action, else_action, get_context());

    list<constraint> resulting_constraints = new base_list<constraint>();
    if (result_type != core_types.unreachable_type()) {
      if (else_action.result() == core_types.unreachable_type()) {
        resulting_constraints = analyzer_utilities.always_by_type(new_constraints,
            constraint_type.ON_TRUE);
        resulting_constraints.append_all(then_constraints);
      } else if (then_action.result() == core_types.unreachable_type()) {
        resulting_constraints = analyzer_utilities.always_by_type(new_constraints,
            constraint_type.ON_FALSE);
        resulting_constraints.append_all(else_constraints);
      } else {
        list<constraint> first = analyzer_utilities.always_by_type(new_constraints,
            constraint_type.ON_TRUE);
        first.append_all(then_constraints);
        list<constraint> second = analyzer_utilities.always_by_type(new_constraints,
            constraint_type.ON_FALSE);
        second.append_all(else_constraints);
        resulting_constraints.append_all(unify_constraint(first, second));
      }
    }

    if (result_type == null) {
      return new error_signal(
          new base_string("Can't unify " + then_action.result() + " and " + else_action.result()),
          the_origin);
    }

    then_action = get_context().promote(then_action, result_type, the_origin);
    else_action = get_context().promote(else_action, result_type, the_origin);

    assert !(then_action instanceof error_signal);
    assert !(else_action instanceof error_signal);

    return action_plus_constraints.make_result(
        new conditional_action(condition_action, then_action, else_action, result_type, the_origin),
        resulting_constraints);
  }

  private readonly_list<constraint> unify_constraint(readonly_list<constraint> first,
      readonly_list<constraint> second) {
    list<constraint> result = new base_list<constraint>();
    set<declaration> processed = new hash_set<declaration>();

    for (int i = first.size() - 1; i >=0; --i) {
      constraint first_constraint = first.get(i);
      declaration the_declaration = first_constraint.the_declaration;
      if (processed.contains(the_declaration)) {
        continue;
      }
      processed.add(the_declaration);
      for (int j = second.size() - 1; j >=0; --j) {
        constraint second_constraint = second.get(j);
        if (second_constraint.the_declaration == the_declaration) {
          // TODO: smarter unification of abstract values.
          if (first_constraint.the_value == second_constraint.the_value) {
            result.append(new constraint(the_declaration, first_constraint.the_value,
                constraint_type.ALWAYS));
            continue;
          }
        }
      }
    }

    return result;
  }

  private analysis_result analyze_with_constraints(analyzable the_analyzable,
      immutable_list<constraint> the_constraints, constraint_type filter) {
    analysis_context new_context = constrained_analysis_context.combine(get_context(),
        analyzer_utilities.always_by_type(the_constraints, filter));
    special_init_context(the_analyzable, new_context);
    return the_analyzable.analyze();
  }

  private void dump_constraints(String name, readonly_list<constraint> constraints) {
    System.out.println(name + ": " + this);
    for (int i = 0; i < constraints.size(); ++i) {
      System.out.println("  " + constraints.get(i));
    }
  }
}

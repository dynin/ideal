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
    if (has_errors(condition)) {
      return new error_signal(messages.error_in_conditional, condition, this);
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
    if (!get_context().can_promote(condition_action.result(), boolean_type)) {
      return new error_signal(new base_string("Boolean value expected, got " +
          condition_action.result()), condition_action);
    }
    condition_action = get_context().promote(condition_action, boolean_type, this);
    // TODO: this check is redundant.
    assert !(condition_action instanceof error_signal);

    analysis_result analyzed_then = analyze_with_constraints(then_branch, new_constraints,
        constraint_type.ON_TRUE);
    if (analyzed_then instanceof error_signal) {
      return new error_signal(messages.error_in_conditional, (error_signal) analyzed_then, this);
    }
    action then_action;
    if (analyzed_then instanceof action) {
      then_action = (action) analyzed_then;
    } else {
      then_action = ((action_plus_constraints) analyzed_then).the_action;
    }

    action else_action;
    readonly_list<constraint> resulting_constraints = new base_list<constraint>();
    if (else_branch != null) {
      analysis_result analyzed_else = analyze_with_constraints(else_branch, new_constraints,
          constraint_type.ON_FALSE);
      if (analyzed_else instanceof error_signal) {
        return new error_signal(messages.error_in_conditional, (error_signal) analyzed_else, this);
      }
      if (analyzed_else instanceof action) {
        else_action = (action) analyzed_else;
      } else {
        else_action = ((action_plus_constraints) analyzed_else).the_action;
      }
    } else {
      if (then_action.result() == core_types.unreachable_type()) {
        resulting_constraints = analyzer_utilities.always_by_type(new_constraints,
            constraint_type.ON_FALSE);
      }
      else_action = library().void_instance().to_action(this);
    }

    @Nullable type result_type = unify(then_action, else_action, get_context());

    if (result_type == null) {
      return new error_signal(
          new base_string("Can't unify " + then_action.result() + " and " + else_action.result()),
          this);
    }

    then_action = get_context().promote(then_action, result_type, this);
    else_action = get_context().promote(else_action, result_type, this);

    assert !(then_action instanceof error_signal);
    assert !(else_action instanceof error_signal);

    return action_plus_constraints.make_result(
        new conditional_action(condition_action, then_action, else_action, result_type, this),
        resulting_constraints);
  }

  // TODO: move this to analyzer_utilities...
  private @Nullable type unify(action first, action second, analysis_context the_context) {
    type first_type = first.result().type_bound();
    type second_type = second.result().type_bound();

    if (first_type == second_type) {
      return first_type;
    } else if (the_context.can_promote(first_type, second_type)) {
      return second_type;
    } else if (the_context.can_promote(second_type, first_type)) {
      return first_type;
    }

    type immutable_void_type = library().immutable_void_type();
    if (the_context.can_promote(first_type, immutable_void_type) &&
        the_context.can_promote(second_type, immutable_void_type)) {
      return immutable_void_type;
    }

    return null;
  }

  private analysis_result analyze_with_constraints(analyzable the_analyzable,
      immutable_list<constraint> the_constraints, constraint_type filter) {
    analysis_context new_context = constrained_analysis_context.combine(get_context(),
        analyzer_utilities.always_by_type(the_constraints, filter));
    special_init_context(the_analyzable, new_context);
    return the_analyzable.analyze();
  }
}

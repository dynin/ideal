/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.switches;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.analyzers.*;
import ideal.development.switches.switch_action.case_clause_action;

public class switch_analyzer extends single_pass_analyzer implements declaration {

  class case_clause {
    public final list<analyzable> case_values;
    public final boolean is_default;
    public final analyzable body;

    public case_clause(list<analyzable> case_values, boolean is_default, analyzable body) {
      this.case_values = case_values;
      this.is_default = is_default;
      this.body = body;
    }
  }

  private analyzable expression;
  private list<case_clause> clauses;
  private @Nullable switch_action the_switch_action;

  // TODO: introduce an inner block
  public switch_analyzer(switch_construct the_switch_action) {
    super(the_switch_action);
    expression = make(the_switch_action.expression);

    clauses = new base_list<case_clause>();
    for (int i = 0; i < the_switch_action.clauses.size(); ++i) {
      case_clause_construct the_construct = the_switch_action.clauses.get(i);
      list<analyzable> case_values = new base_list<analyzable>();
      boolean is_default = false;
      for (int j = 0; j < the_construct.cases.size(); ++j) {
        case_construct the_case_construct = the_construct.cases.get(j);
        if (the_case_construct.case_value != null) {
          case_values.append(make(the_case_construct.case_value));
        } else {
          is_default = true;
        }
      }
      analyzable body = new list_analyzer(make_list(the_construct.body), this);
      clauses.append(new case_clause(case_values, is_default, body));
    }
  }

  public switch_action get_switch_action() {
    assert the_switch_action != null;
    return the_switch_action;
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();

    result.append(expression);
    for (int i = 0; i < clauses.size(); ++ i) {
      result.append_all(clauses.get(i).case_values);
      result.append(clauses.get(i).body);
    }

    return result;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    origin the_origin = this;

    if (has_analysis_errors(expression)) {
      return new error_signal(new base_string("Error in switch expression"), expression,
          the_origin);
    }

    action expression_action = analyzer_utilities.to_value(expression.analyze().to_action(),
        get_context(), the_origin);
    type the_type = expression_action.result().type_bound();
    boolean is_enum = the_type.principal().get_kind() == type_kinds.enum_kind;

    // TODO: detect duplicate values
    list<case_clause_action> clause_actions = new base_list<case_clause_action>();
    for (int i = 0; i < clauses.size(); ++i) {
      case_clause the_clause = clauses.get(i);
      list<data_value> values = new base_list<data_value>();
      for (int j = 0; j < the_clause.case_values.size(); ++j) {
        analyzable the_analyzable = the_clause.case_values.get(j);
        if (is_enum && the_analyzable instanceof resolve_analyzer) {
          resolve_analyzer the_resolve_analyzer = (resolve_analyzer) the_analyzable;
          if (!the_resolve_analyzer.has_from()) {
            the_analyzable = new resolve_analyzer(
                base_analyzable_action.from(the_type.principal(), the_origin),
                the_resolve_analyzer.short_name(),
                the_resolve_analyzer);
            the_clause.case_values.set(j, the_analyzable);
          }
        }
        if (has_analysis_errors(the_analyzable)) {
          return new error_signal(new base_string("Error in switch expression"), the_analyzable,
              the_origin);
        }
        action the_action = the_analyzable.analyze().to_action();
        if (!get_context().can_promote(the_action, the_type)) {
          return action_utilities.cant_promote(the_action.result(), the_type, the_origin);
        }
        // TODO: handle expressions in case values.
        //the_action = get_context().promote(the_action, the_type, the_origin);
        if (!(the_action instanceof data_value_action)) {
          return new error_signal(new base_string("Data value expected in switch"), the_action);
        }
        data_value the_data_value = (data_value) the_action.result();
        values.append(the_data_value);
      }
      if (has_analysis_errors(the_clause.body)) {
        return new error_signal(new base_string("Error in switch body"), the_clause.body,
            the_origin);
      }
      // TODO: handle breaks
      // TODO: handle variables in body
      // TODO: handle non-terminating bodies
      action body = the_clause.body.analyze().to_action();
      if (body.result().type_bound() != common_types.unreachable_type()) {
        return new error_signal(new base_string("TODO: handle fallthrough in switch"), body);
      }
      case_clause_action the_case_clause_action = new case_clause_action(values,
          the_clause.is_default, body);
      clause_actions.append(the_case_clause_action);
    }

    the_switch_action = new switch_action(expression_action, clause_actions, the_origin);

  /*
    if (inside == null) {
      inside = make_block(LOOP_NAME, this);
    }
    */

    return the_switch_action;
  }
}

/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.switches;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.machine.elements.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.jumps.*;
import ideal.development.values.*;
import ideal.development.actions.*;

import javax.annotation.Nullable;

public class switch_action extends base_action {
  public static class case_clause_action {
    public final readonly_list<data_value> case_values;
    public final boolean is_default;
    public final action body;

    public case_clause_action(readonly_list<data_value> case_values, boolean is_default,
        action body) {
      this.case_values = case_values;
      this.is_default = is_default;
      this.body = body;
    }
  }

  public final action expression;
  public final readonly_list<case_clause_action> clauses;

  // TODO: handle break construct
  public switch_action(action expression, readonly_list<case_clause_action> clauses,
      origin the_origin) {
    super(the_origin);
    this.expression = expression;
    this.clauses = clauses;
  }

  @Override
  public abstract_value result() {
    for (int i = 0; i < clauses.size(); ++i) {
      // TODO: handle reachable cases
      assert clauses.get(i).body.result() == common_types.unreachable_type();
    }

    return common_types.unreachable_type();
  }

  @Override
  public boolean has_side_effects() {
    for (int i = 0; i < clauses.size(); ++i) {
      if (clauses.get(i).body.has_side_effects()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context exec_context) {
    assert from_entity instanceof null_wrapper;

    entity_wrapper expression_result = expression.execute(null_wrapper.instance, exec_context);
    if (expression_result instanceof jump_wrapper) {
      return expression_result;
    }

    Object expression_value = ((value_wrapper) expression_result).unwrap();
    @Nullable Integer clause_index = null;
    boolean done = false;
    for (int i = 0; i < clauses.size(); ++i) {
      case_clause_action clause = clauses.get(i);
      for (int j = 0; j < clause.case_values.size(); ++j) {
        if (runtime_util.data_equals(expression_value, clause.case_values.get(j).unwrap())) {
          clause_index = i;
          done = true;
          break;
        }
      }
      if (done) {
        break;
      }
      if (clause.is_default) {
        clause_index = i;
      }
    }

    if (clause_index != null) {
      // TODO: handle fallthrough
      return clauses.get(clause_index).body.execute(null_wrapper.instance, exec_context);
    } else {
      return common_values.void_instance();
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, expression);
  }
}

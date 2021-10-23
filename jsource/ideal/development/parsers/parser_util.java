/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.parsers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.constructs.*;
import ideal.development.scanners.*;
import ideal.development.names.*;
import ideal.development.origins.*;

import javax.annotation.Nullable;

public class parser_util {

  public static origin empty_origin = new special_origin(new base_string("empty"));

  public static construct maybe_variable(readonly_list<annotation_construct> annotations,
      construct expression, origin the_origin) {
    if (annotations.is_not_empty()) {
      return new variable_construct(annotations, expression, null,
          new empty<annotation_construct>(), null, the_origin);
    } else {
      return expression;
    }
  }

  /*
  public static void ensure_empty(readonly_list<annotation_construct> seq) {
    if (seq.is_not_empty()) {
      new base_notification(messages.unexpected_modifier, seq.first()).report();
    }
  }
  */

  public static construct expr_or_ctor(list<annotation_construct> annotations, construct expression,
      @Nullable construct body) {
    if (expression instanceof parameter_construct) {
      parameter_construct pc = (parameter_construct) expression;
      if (annotations.is_not_empty() || body != null || has_variables(pc.parameters)) {
        // TODO: notify user instead of failing cast
        name_construct nc = (name_construct) pc.main;
        // TODO: origin...
        return new procedure_construct(annotations, null, nc.the_name, pc.parameters,
            new empty<annotation_construct>(), body, expression.deeper_origin());
      }
    }
    return expression;
  }

  // TODO: rewrite with list methods..
  private static boolean has_variables(readonly_list<construct> constructs) {
    for (int i = 0; i < constructs.size(); ++i) {
      if (constructs.get(i) instanceof variable_construct) {
        return true;
      }
    }
    return false;
  }

  // TODO: introduce operator construct
  public static construct make_op(construct e1, token op_token, construct e2, operator op) {
    assert op.the_operator_type == operator_type.INFIX;
    origin op_origin = new fragment_origin(e1, op_token, e2);
    return new operator_construct(op, e1, e2, op_origin);
  }

  public static construct make_op(token op_token, construct e, operator op) {
    return new operator_construct(op, e, new fragment_origin(op_token, op_token, e));
  }

  public static @Nullable readonly_list<construct> type_parameters(
      @Nullable list_construct constructs) {

    if (constructs == null) {
      return null;
    }

    // TODO: signal error instead of failing an assertion.
    assert constructs.the_elements.is_not_empty();
    assert constructs.grouping == grouping_type.BRACKETS;
    assert !constructs.has_trailing_comma;

    return constructs.the_elements;
  }

  public static @Nullable readonly_list<construct> procedure_parameters(
      @Nullable list_construct constructs) {

    if (constructs == null) {
      return null;
    }

    // TODO: signal error instead of failing an assertion.
    assert constructs.grouping == grouping_type.PARENS;
    assert !constructs.has_trailing_comma;

    return constructs.the_elements;
  }

  public static parameter_construct make_parameter(construct main, list_construct constructs,
      origin the_origin) {

    // TODO: signal error instead of failing an assertion.
    assert constructs.grouping == grouping_type.PARENS ||
           constructs.grouping == grouping_type.BRACKETS;
    assert !constructs.has_trailing_comma;

    return new parameter_construct(main, constructs.the_elements, constructs.grouping, the_origin);
  }
}

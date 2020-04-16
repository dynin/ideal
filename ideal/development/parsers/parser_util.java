/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

import javax.annotation.Nullable;

public class parser_util {

  public static position empty_position = new special_position(new base_string("empty"));

  public static void ensure_empty(readonly_list<annotation_construct> seq) {
    if (!seq.is_empty()) {
      new base_notification(messages.unexpected_modifier, seq.get(0)).report();
    }
  }

  public static construct expr_or_ctor(list<annotation_construct> annotations, construct expression,
      @Nullable construct body) {
    if (expression instanceof parameter_construct) {
      parameter_construct pc = (parameter_construct) expression;
      if (!annotations.is_empty() || body != null || has_variables(pc.parameters.elements)) {
        // TODO: notify user instead of failing cast
        name_construct nc = (name_construct) pc.main;
        // TODO: position...
        return new procedure_construct(annotations, null, nc.the_name, pc.parameters,
            new empty<annotation_construct>(), body, expression.source_position());
      }
    }
    return expression;
  }

  // TODO: rewrite with list methods..
  private static boolean has_variables(readonly_list<construct> constructs) {
    for (int i = 0; i < constructs.size(); ++i) {
      if (constructs.get(0) instanceof variable_construct) {
        return true;
      }
    }
    return false;
  }

  // TODO: introduce operator construct
  public static construct make_op(construct e1, token op_token, construct e2, operator op) {
    assert op.type == operator_type.INFIX || op.type == operator_type.ASSIGNMENT;
    position op_position = new fragment_position(e1, op_token, e2);
    return new operator_construct(op, e1, e2, op_position);
  }

  public static construct make_op(token op_token, construct e, operator op) {
    return new operator_construct(op, e, new fragment_position(op_token, op_token, e));
  }
}

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.targets;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.analyzers.*;
import ideal.development.values.*;

public class target_declaration extends declaration_analyzer<target_construct> {

  private final analyzable expression;
  private action target_action;

  public target_declaration(target_construct source) {
    super(source);
    expression = make(source.expression);
  }

  public simple_name short_name() {
    return source.name;
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {
    analyze_and_ignore_errors(expression, pass);
    
    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      if (has_errors(expression)) {
        return new error_signal(new base_string("Error in target expression"), expression, this);
      }

      target_action = action_not_error(expression);
    }

    return null;
  }

  public void process() {
    analyze();

    if (target_action == null) {
      // TODO: report error
      return;
    }

    assert target_action instanceof bound_procedure;
    bound_procedure bound_target = (bound_procedure) target_action;

    abstract_value the_target_value = bound_target.the_procedure_action.result();
    assert the_target_value instanceof target_value;
    target_value the_target = (target_value) the_target_value;

    the_target.process(bound_target.parameters, get_context());
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}

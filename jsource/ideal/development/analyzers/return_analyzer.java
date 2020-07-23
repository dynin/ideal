/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class return_analyzer extends single_pass_analyzer {

  public final @Nullable analyzable the_expression;
  public @Nullable procedure_declaration the_procedure;
  private @Nullable type return_type;
  private @Nullable action return_expr;

  public return_analyzer(return_construct source) {
    super(source);
    the_expression = make(source.the_expression);
  }

  public return_analyzer(analyzable the_expression, origin source) {
    super(source);
    this.the_expression = the_expression;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    // TODO: handle null return, check return type.
    assert the_expression != null;
    if (has_errors(the_expression)) {
      return new error_signal(new base_string("Error in return expression"), the_expression, this);
    }

    // hmm... frame.add(where, special_name.RETURN, return_value);

    the_procedure = analyzer_utilities.get_enclosing_procedure(this);
    if (the_procedure == null) {
      return new error_signal(messages.return_outside_proc, this);
    }

    if (the_procedure.has_errors()) {
      // The return type might be not set
      return library().void_instance().to_action(this);
    }

    // TODO: add support for return type inference...
    return_type = the_procedure.get_return_type();

    if (analyzer_utilities.is_readonly_reference(return_type)) {
      return_type = library().get_reference_parameter(return_type);
    }

    return_expr = action_not_error(the_expression);
    type_utilities.prepare(return_expr.result(), declaration_pass.METHODS_AND_VARIABLES);

    if (!get_context().can_promote(return_expr, return_type)) {
      return action_utilities.cant_promote(return_expr.result(), return_type,
          get_context(), this);
    }
    return_expr = get_context().promote(return_expr, return_type, this);

    return new return_action(return_expr, the_procedure, return_type, this);
  }

  public type return_type() {
    assert return_type != null;
    return return_type;
  }

  public action return_expression() {
    assert return_expr != null;
    return return_expr;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_expression);
  }
}

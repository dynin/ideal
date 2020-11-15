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
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.declarations.*;

public class flavor_analyzer extends single_pass_analyzer {

  public final type_flavor flavor;
  public final analyzable expression;
  public type flavored_type;

  private flavor_analyzer(type_flavor flavor, analyzable expression, origin source) {
    super(source);
    this.flavor = flavor;
    this.expression = expression;
  }

  public flavor_analyzer(flavor_construct source) {
    this(source.flavor, make(source.expr), source);
  }

  @Override
  protected void do_add_dependence(@Nullable principal_type the_principal, declaration_pass pass) {
    add_dependence(expression, the_principal, pass);
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    @Nullable error_signal expr_error = find_error(expression);

    if (expr_error != null) {
      return new error_signal(messages.error_in_flavored_type, expr_error, this);
    }

    action expr_action = action_not_error(expression);

    // TODO: does this handle all cases?
    if (! (expr_action instanceof type_action)) {
      return new error_signal(messages.type_expected, expression);
    }

    type the_type = ((type_action) expr_action).get_type();
    type_utilities.prepare(the_type, declaration_pass.FLAVOR_PROFILE);
    flavored_type = the_type.get_flavored(flavor);
    return flavored_type.to_action(this);
  }

  @Override
  public flavor_analyzer specialize(specialization_context context, principal_type new_parent) {
    analyzable new_expression = expression.specialize(context, new_parent);
    flavor_analyzer result = new flavor_analyzer(flavor, new_expression, this);
    result.set_context(new_parent, get_context());
    return result;
  }
}

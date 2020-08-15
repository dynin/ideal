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
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.declarations.*;

public class parameter_analyzer extends single_pass_analyzer {

  public final analyzable main_analyzable;
  public final readonly_list<analyzable> analyzable_parameters;

  public @Nullable action main_action;
  private @Nullable action_parameters aparams;
  private declaration_pass parameter_pass;

  public parameter_analyzer(analyzable main, readonly_list<analyzable> params, origin pos) {
    super(pos);
    this.main_analyzable = main;
    this.analyzable_parameters = params;
    this.parameter_pass = declaration_pass.METHODS_AND_VARIABLES;
  }

  public parameter_analyzer(parameter_construct source) {
    this(make(source.main), make_list(source.parameters.elements), source);
  }

  public parameter_analyzer(operator_construct source) {
    super(source);
    main_analyzable = new resolve_analyzer(source.the_operator, this);
    analyzable_parameters = make_list(source.arguments);
    this.parameter_pass = declaration_pass.METHODS_AND_VARIABLES;
  }

  @Override
  protected void do_add_dependence(@Nullable principal_type the_principal, declaration_pass pass) {
    add_dependence(main_analyzable, the_principal, pass);
    for (int i = 0; i < analyzable_parameters.size(); ++i) {
      analyzable the_parameter = analyzable_parameters.get(i);
      add_dependence(the_parameter, the_principal, pass);
    }
    this.parameter_pass = pass;
  }

  public @Nullable action_parameters get_parameters() {
    return aparams;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    if (is_logical_and()) {
      analyzable logical_and_conditional = rewrite_logical_and();
      init_context(logical_and_conditional);
      return logical_and_conditional.analyze();
    }

    list<action> param_actions = new base_list<action>();
    error_signal error = null;
    for (int i = 0; i < analyzable_parameters.size(); ++i) {
      analyzable param = analyzable_parameters.get(i);
      @Nullable error_signal arg_error = find_error(param);
      if (arg_error != null) {
        if (error == null) {
          error = arg_error;
        }
      } else {
        action the_action = action_not_error(param);
        type_utilities.prepare(the_action.result(), parameter_pass);
        param_actions.append(the_action);
      }
    }

    if (error != null) {
      return new error_signal(messages.error_in_parametrizable, error, this);
    }
    aparams = new action_parameters(param_actions);

    @Nullable error_signal main_error = find_error(main_analyzable);
    if (main_error != null) {
      return new error_signal(messages.error_in_parametrizable, main_error, this);
    }

    action main_action = action_not_error(main_analyzable);
    if (!analyzer_utilities.is_parametrizable(main_action.result(), aparams, get_context())) {
      type result_type = main_action.result().type_bound();
      type_utilities.prepare(result_type, parameter_pass);
      readonly_list<action> implicit_results = get_context().resolve(result_type,
          special_name.IMPLICIT_CALL, main_analyzable);

      if (implicit_results.is_empty()) {
        return mismatch_reporter.signal_mismatch(main_action, aparams, get_context(), this);
      }

      if (implicit_results.size() > 1) {
        return mismatch_reporter.signal_not_matching(implicit_results, aparams, get_context(),
            this);
      }

      if (implicit_results.size() == 1) {
        action implicit_action = implicit_results.first().bind_from(main_action, main_analyzable);
        if (!analyzer_utilities.is_parametrizable(implicit_action.result(), aparams,
            get_context())) {
          return mismatch_reporter.signal_mismatch(implicit_action, aparams, get_context(),
            this);
        }
        main_action = implicit_action;
      } else {
        return mismatch_reporter.signal_mismatch(main_action, aparams, get_context(), this);
      }
    }

    analysis_result result = analyzer_utilities.bind_parameters(main_action, aparams,
        get_context(), this);

    if (result instanceof action) {
      type result_type = ((action) result).result().type_bound();
      type_utilities.prepare(result_type, parameter_pass);
    }

    return result;
  }

  private boolean is_logical_and() {
    return main_analyzable instanceof resolve_analyzer &&
        ((resolve_analyzer) main_analyzable).short_name() == operator.LOGICAL_AND;
  }

  private conditional_analyzer rewrite_logical_and() {
    origin the_origin = this;
    assert analyzable_parameters.size() == 2;
    analyzable first = analyzable_parameters.get(0);
    analyzable second = analyzable_parameters.get(1);
    analyzable false_value = base_analyzable_action.from(library().false_value(), the_origin);
    return new conditional_analyzer(first, second, false_value, the_origin);
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    @Nullable error_signal main_error = find_error(main_analyzable);
    if (main_error != null) {
      return main_error;
    }

    analyzable new_main = main_analyzable.specialize(context, new_parent);
    list<analyzable> new_parameters = new base_list<analyzable>();

    for (int i = 0; i < analyzable_parameters.size(); ++i) {
      analyzable parameter = analyzable_parameters.get(i);
      analyzable specialized = parameter.specialize(context, new_parent);
      new_parameters.append(specialized);
    }

    parameter_analyzer result = new parameter_analyzer(new_main, new_parameters, this);
    result.set_context(new_parent, get_context());
    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, main_analyzable);
  }
}

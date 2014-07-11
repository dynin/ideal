/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

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
import ideal.development.declarations.*;

public class resolve_analyzer extends single_pass_analyzer {

  public static boolean CURE_UNDECLARED;
  public static boolean HIDE_DECLARATIONS;

  private @Nullable analyzable from;
  private @Nullable construct the_name_construct;
  private @Nullable action_name the_name;
  private @Nullable action main_candidate;
  private declaration_pass resolve_pass;

  public resolve_analyzer(@Nullable analyzable from, action_name the_name, position source) {
    super(source);
    this.the_name = the_name;
    this.from = from;
    this.resolve_pass = declaration_pass.METHODS_AND_VARIABLES;
  }

  public resolve_analyzer(resolve_construct source) {
    super(source);
    from = make(source.qualifier);
    the_name_construct = source.name;
    this.resolve_pass = declaration_pass.METHODS_AND_VARIABLES;
  }

  public resolve_analyzer(name_construct source) {
    this(null, source.the_name, source);
  }

  public resolve_analyzer(action_name the_name, position source) {
    this(null, the_name, source);
  }

  public action_name short_name() {
    assert the_name != null;
    return the_name;
  }

  public boolean has_from() {
    return from != null;
  }

  @Override
  protected void do_add_dependence(@Nullable principal_type the_principal, declaration_pass pass) {
    this.resolve_pass = pass;
    if (from != null) {
      add_dependence(from, the_principal, pass);
    }
    analysis_result result = analyze();
    if (result instanceof type_action) {
      principal_type the_type = ((type_action) result).get_type().principal();
      add_type_dependence(the_principal, the_type);
    }
  }

  public analysis_result resolve(@Nullable action_target the_action_target) {
    if (has_saved_result()) {
      return analyze();
    } else {
      analysis_result result = do_resolve_and_narrow(the_action_target);
      set_saved_result(result);
      return result;
    }
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    return do_resolve_and_narrow(null);
  }

  private analysis_result do_resolve_and_narrow(@Nullable action_target the_action_target) {
    analysis_result result = do_resolve(the_action_target);

    if (! (result instanceof error_signal)) {
      @Nullable declaration the_declaration = declaration_util.get_declaration(result);
      if (the_declaration != null) {
        @Nullable abstract_value narrowed = get_context().lookup_constraint(the_declaration);
        if (narrowed != null) {
          variable_declaration the_variable_declaration = (variable_declaration) the_declaration;
          if (narrowed.type_bound() != the_variable_declaration.reference_type()) {
            action variable_access = (action) result;
            return new narrow_action(variable_access, narrowed.type_bound(),
                the_variable_declaration, this);
          }
        }
      }
    }

    return result;
  }

  private analysis_result do_resolve(@Nullable action_target the_action_target) {
    type from_type;

    if (from != null) {
      if (has_errors(from)) {
        return new error_signal(messages.error_in_source, from, this);
      }
      from_type = action_not_error(from).result().type_bound();
    } else {
      assert the_name != null;
      from_type = (the_name instanceof operator) ? library().operators_package() : parent();
    }

    if (the_name == null) {
      assert the_name_construct != null;
      associate_with_this(the_name_construct);
      if (!(the_name_construct instanceof name_construct)) {
        return new error_signal(messages.identifier_expected, the_name_construct);
      }
      the_name = ((name_construct) the_name_construct).the_name;
    }

    readonly_list<action> all_resolved = get_context().resolve(from_type, the_name,
        the_action_target, this);

    if (all_resolved.is_empty()) {
      error_signal error;
      if (from == null && CURE_UNDECLARED) {
        error = new error_signal(messages.symbol_lookup_suppress, this);
        error_signal suppressed_error = new error_signal(messages.suppressed, error, this);
        add_error(parent(), the_name, suppressed_error);
      } else {
        error = mismatch_reporter.signal_lookup_failure(the_name, from_type, the_action_target,
            get_context(), this);
      }
      return error;
    }

    assert !all_resolved.is_empty();

    if (all_resolved.size() > 1) {
      readonly_list<notification> declarations;
      if (HIDE_DECLARATIONS) {
        declarations = null;
      } else {
        if (the_action_target != null) {
          return mismatch_reporter.signal_not_matching(all_resolved, the_action_target,
              get_context(), this);
        }
        // TODO: fix this by introducing a stable order
        declarations = notification_util.to_notifications(all_resolved, get_context());
      }
      notification ambiguous = new base_notification(messages.ambiguous_access, this, declarations);
      return new error_signal(ambiguous, false);
    }

    assert all_resolved.size() == 1;
    main_candidate = all_resolved.get(0);
    if (from != null) {
      // TODO: don't convert from to action twice...
      main_candidate = main_candidate.bind_from(action_not_error(from), this);
    }

    action result;

    if (main_candidate instanceof error_signal ||
        the_name == special_name.IMPLICIT_CALL ||
        the_action_target == null ||
        the_action_target.matches(main_candidate.result())) {
      result = main_candidate;
    } else {
      type result_type = main_candidate.result().type_bound();
      type_utilities.prepare(result_type, resolve_pass);
      readonly_list<action> implicit_results = get_context().resolve(
          result_type, special_name.IMPLICIT_CALL, the_action_target, this);

      if (implicit_results.is_empty()) {
        return mismatch_reporter.signal_not_matching(all_resolved, the_action_target,
            get_context(), this);
      }

      if (implicit_results.size() > 1) {
        return mismatch_reporter.signal_not_matching(implicit_results, the_action_target,
            get_context(), this);
      }

      result = implicit_results.get(0).bind_from(main_candidate, this);
    }

    type_utilities.prepare(result.result(), resolve_pass);

    return result;
  }

  public @Nullable action get_main_candidate() {
    return main_candidate;
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    analysis_result the_result = analyze();
    if (the_result instanceof error_signal) {
      return (error_signal) the_result;
    }
    // TODO: avoid cast here.
    action the_action = (action) the_result;
    declaration source_declaration = declaration_util.get_declaration(the_action);
    if (source_declaration instanceof type_parameter_declaration) {
      type_parameter_declaration parameter_decl = (type_parameter_declaration) source_declaration;
      master_type param_type = (master_type) parameter_decl.get_declared_type();
      @Nullable abstract_value specialized = context.lookup(param_type);
      if (specialized != null) {
        return analyzable_action.from_value(specialized, this);
      }
    }
    return new analyzable_action(the_action, this);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_name);
  }
}

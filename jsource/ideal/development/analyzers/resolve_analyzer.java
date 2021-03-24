/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
  private @Nullable action_name the_name;
  private @Nullable action main_candidate;
  private declaration_pass resolve_pass;

  public resolve_analyzer(@Nullable analyzable from, action_name the_name, origin source) {
    super(source);
    this.the_name = the_name;
    this.from = from;
    this.resolve_pass = declaration_pass.METHODS_AND_VARIABLES;
  }

  public resolve_analyzer(resolve_construct source) {
    super(source);
    from = make(source.qualifier);
    the_name = source.the_name;
    this.resolve_pass = declaration_pass.METHODS_AND_VARIABLES;
  }

  public resolve_analyzer(name_construct source) {
    this(null, source.the_name, source);
  }

  public resolve_analyzer(action_name the_name, origin source) {
    this(null, the_name, source);
  }

  public action_name short_name() {
    return the_name;
  }

  public boolean has_from() {
    return from != null;
  }

  public @Nullable analyzable get_from() {
    return from;
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

  @Override
  public readonly_list<analyzable> children() {
    if (from != null) {
      return new base_list<analyzable>(from);
    } else {
      return new empty<analyzable>();
    }
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    origin the_origin = this;
    type from_type;
    action from_action;

    if (from != null) {
      if (has_analysis_errors(from)) {
        return new error_signal(messages.error_in_source, from, the_origin);
      }
      from_action = analyzer_utilities.to_value(action_not_error(from), get_context(), the_origin);
      from_type = from_action.result().type_bound();
    } else {
      assert the_name != null;
      from_action = null;
      from_type = (the_name instanceof operator) ? library().operators_package() : parent();
    }

    declaration the_declaration = from_type.principal().get_declaration();
    if (the_declaration instanceof type_announcement) {
      ((type_announcement) the_declaration).load_type();
    }

    readonly_list<action> all_resolved = get_context().resolve(from_type, the_name, the_origin);

    if (all_resolved.is_empty()) {
      error_signal error;
      if (from == null && CURE_UNDECLARED) {
        error = new error_signal(messages.symbol_lookup_suppress, this);
        error_signal suppressed_error = new error_signal(messages.suppressed, error, the_origin);
        add_error(parent(), the_name, suppressed_error);
      } else {
        error = mismatch_reporter.signal_lookup_failure(the_name, from_type, null,
            get_context(), the_origin);
      }
      return error;
    }

    assert all_resolved.is_not_empty();

    if (all_resolved.size() > 1) {
      readonly_list<notification> declarations;
      if (HIDE_DECLARATIONS) {
        declarations = null;
      } else {
        // TODO: fix this by introducing a stable order
        declarations = notification_util.to_notifications(all_resolved, get_context());
      }
      notification ambiguous = new base_notification(messages.ambiguous_access, this, declarations);
      return new error_signal(ambiguous, false);
    }

    assert all_resolved.size() == 1;
    main_candidate = all_resolved.first();

    if (from != null) {
      // TODO: don't convert from to action twice...
      main_candidate = main_candidate.bind_from(from_action, the_origin);
    }

    type_utilities.prepare(main_candidate.result(), resolve_pass);

    return main_candidate;
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
        return base_analyzable_action.from(specialized, this);
      }
    }
    return new base_analyzable_action(the_action);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_name);
  }
}

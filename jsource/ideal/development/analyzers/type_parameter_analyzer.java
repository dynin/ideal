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
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class type_parameter_analyzer extends declaration_analyzer
    implements type_parameter_declaration {

  private final @Nullable analyzable parameter_analyzable;
  private final action_name the_name;
  private @Nullable type var_type;
  private master_type new_master;
  private boolean declaration_analysis_in_progress;
  private @Nullable list<declaration> signature;

  public type_parameter_analyzer(variable_construct source) {
    super(source);
    // TODO: process modifiers here...
    //process_modifiers(source.modifiers, access_modifier.public_modifier);
    parameter_analyzable = make(source.type);
    the_name = source.name;
  }

  public type_parameter_analyzer(action_name the_name, origin source) {
    super(source);
    this.parameter_analyzable = null;
    this.the_name = the_name;
  }

  @Override
  public kind get_kind() {
    return type_kinds.type_alias_kind;
  }

  @Override
  public action_name short_name() {
    return the_name;
  }

  @Override
  public type variable_type() {
    assert var_type != null;
    return var_type;
  }

  @Override
  public principal_type get_declared_type() {
    assert new_master != null;
    return new_master;
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL) {
      // TODO: enforce that varargs_modifier is the only modifier that was
      //  specified, and public wasn't overriden

      new_master = action_utilities.make_type(get_context(), get_kind(), null,
          short_name(), parent(), this, this);

      if (parameter_analyzable != null) {
        add_dependence(parameter_analyzable, new_master, declaration_pass.TYPES_AND_PROMOTIONS);
      }
    } else if (pass == analysis_pass.SUPERTYPE_DECL) {
      assert new_master != null;
      assert var_type == null;

      @Nullable error_signal signal = process_parameter();
      if (signal != null) {
        var_type = library().value_type().get_flavored(flavors.any_flavor);
        maybe_report_error(signal);
      } else {
        assert var_type != null;
      }

      assert !declaration_analysis_in_progress;
      declaration_analysis_in_progress = true;

      assert !new_master.has_flavor_profile();
      new_master.set_flavor_profile(var_type.principal().get_flavor_profile());
      get_context().add(new_master, special_name.SUPERTYPE, var_type.to_action(this));
      get_context().add(new_master, special_name.TYPE_ALIAS, var_type.to_action(this));

      declaration_analysis_in_progress = false;
    }

    return null;
  }

  @Override
  public void process_declaration(declaration_pass pass) {
    if (pass == declaration_pass.TYPES_AND_PROMOTIONS) {
      if (!declaration_analysis_in_progress) {
        multi_pass_analysis(analysis_pass.SUPERTYPE_DECL);
      }
    } else if (pass == declaration_pass.METHODS_AND_VARIABLES) {
      // do nothing
    } else {
      utilities.panic("Unknown pass: " + pass);
    }
  }

  @Override
  public readonly_list<declaration> get_signature() {
    if (signature == null) {
      signature = new base_list<declaration>();
    }

    return signature;
  }

  private @Nullable error_signal process_parameter() {
    if (parameter_analyzable == null) {
      return new error_signal(messages.var_type_expected, this);
    }

    @Nullable error_signal error = find_error(parameter_analyzable);

    if (error != null) {
      return new error_signal(messages.error_in_var_type, error, parameter_analyzable);
    }

    action the_action = action_not_error(parameter_analyzable);

    if (! (the_action instanceof type_action)) {
      return new error_signal(messages.type_expected, parameter_analyzable);
    }

    var_type = handle_default_any_flavor(((type_action) the_action).get_type());
    return null;
  }

  private static type handle_default_any_flavor(type the_type) {
    if (the_type instanceof principal_type) {
      principal_type principal = the_type.principal();
      return principal.get_flavored(flavors.any_flavor);
    } else {
      return the_type;
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}

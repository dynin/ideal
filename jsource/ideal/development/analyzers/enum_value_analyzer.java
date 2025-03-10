/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

public class enum_value_analyzer extends declaration_analyzer implements variable_declaration {

  private final action_name the_name;
  private final @Nullable readonly_list<construct> parameters;
  private final origin the_name_origin;
  private final @Nullable origin the_parameter_origin;
  private final int ordinal;
  private @Nullable parameter_analyzer constructor_call;
  private @Nullable action_parameters the_action_parameters;

  public enum_value_analyzer(construct the_construct, int ordinal) {
    super(the_construct);
    assert enum_util.can_be_enum_value(the_construct);
    if (the_construct instanceof name_construct) {
      the_name = ((name_construct) the_construct).the_name;
      parameters = null;
      the_name_origin = the_construct;
      the_parameter_origin = null;
    } else if (the_construct instanceof variable_construct) {
      variable_construct the_variable_construct = (variable_construct) the_construct;
      the_name = the_variable_construct.name;
      parameter_construct the_parameter_construct =
          (parameter_construct) the_variable_construct.init;
      parameters = the_parameter_construct.parameters;
      the_name_origin = the_parameter_construct.main;
      the_parameter_origin = the_parameter_construct;
    } else {
      utilities.panic("Unrecognized enum value declaration: " + the_construct);
      // To silence javac
      the_name = null;
      parameters = null;
      the_name_origin = null;
      the_parameter_origin = null;
    }
    this.ordinal = ordinal;
  }

  @Override
  public action_name short_name() {
    return the_name;
  }

  @Override
  public readonly_list<declaration> get_overriden() {
    return new empty<declaration>();
  }

  @Override
  public variable_category get_category() {
    return variable_category.ENUM_VALUE;
  }

  @Override
  public type_flavor get_flavor() {
    return flavor.nameonly_flavor;
  }

  @Override
  public type value_type() {
    return declared_in_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  @Override
  public type reference_type() {
    return common_types.get_reference(flavor.deeply_immutable_flavor, value_type());
  }

  @Override
  public boolean declared_as_reference() {
    return false;
  }

  public boolean has_parameters() {
    return the_action_parameters != null;
  }

  public @Nullable action_parameters get_parameters() {
    return the_action_parameters;
  }

  @Override
  public @Nullable action init_action() {
    return null;
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();

    result.append(annotations());
    if (constructor_call != null) {
      result.append(constructor_call);
    }

    return result;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      set_annotations(new base_annotation_set(access_modifier.public_modifier, null,
          new hash_set<modifier_kind>().frozen_copy(), null, new empty<origin>()));
      // TODO: ordinal should be correctly computed.
      enum_value the_value = new enum_value(this, ordinal, value_type());
      get_context().add(declared_in_type(), short_name(), the_value.to_action(this));
      // TODO: handle autoimport of boolean enums.
      get_context().add(declared_in_type().get_parent(), short_name(), the_value.to_action(this));
    }

    if (pass == analysis_pass.BODY_CHECK && parameters != null) {
      origin the_origin = this;
      analyzable allocate = new base_analyzable_action(
          new allocate_action(declared_in_type(), the_origin));
      analyzable ctor_expression = new resolve_analyzer(allocate, special_name.IMPLICIT_CALL,
          the_name_origin);
      readonly_list<analyzable> the_constructor_parameters = make_list(parameters);
      constructor_call = new parameter_analyzer(ctor_expression, the_constructor_parameters,
          the_parameter_origin);
      if (!has_analysis_errors(constructor_call, pass)) {
        the_action_parameters = constructor_call.get_parameters();
      }
    }

    return ok_signal.instance;
  }

  @Override
  public enum_value_analyzer specialize(specialization_context new_context,
      principal_type new_parent) {
    // TODO: set new parent.
    utilities.panic("enum_value_analyzer.specialize()");
    return this;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}

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
  private final @Nullable list_construct parameters;
  private final int ordinal;

  public enum_value_analyzer(construct the_construct, int ordinal) {
    super(the_construct);
    assert enum_util.can_be_enum_value(the_construct);
    if (the_construct instanceof name_construct) {
      the_name = ((name_construct) the_construct).the_name;
      parameters = null;
    } else {
      parameter_construct the_parameter_construct = (parameter_construct) the_construct;
      // TODO: do not panic, report an error here.
      the_name = ((name_construct) the_parameter_construct.main).the_name;
      // TODO: check for empty parameters...
      parameters = the_parameter_construct.parameters;
    }
    this.ordinal = ordinal;
  }

  @Override
  public action_name short_name() {
    return the_name;
  }

  @Override
  public variable_category get_category() {
    return variable_category.ENUM_VALUE;
  }

  @Override
  public type value_type() {
    return declared_in_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  @Override
  public type reference_type() {
    return library().get_reference(flavor.deeply_immutable_flavor, value_type());
  }

  @Override
  public @Nullable analyzable initializer() {
    return null;
  }

  @Override
  public @Nullable action init_action() {
    return null;
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      set_annotations(new base_annotation_set(access_modifier.public_modifier,
          new hash_set<modifier_kind>(), null));
      // TODO: ordinal should be correctly computed.
      enum_value the_value = new enum_value(this, ordinal, value_type());
      get_context().add(declared_in_type(), short_name(), the_value.to_action(this));
      // TODO: handle autoimport of boolean enums.
      get_context().add(declared_in_type().get_parent(), short_name(), the_value.to_action(this));
    }

    if (pass == analysis_pass.BODY_CHECK && parameters != null) {
      origin pos = this;
      analyzable allocate = new analyzable_action(new allocate_action(declared_in_type(), pos));
      analyzable ctor_expression = new resolve_analyzer(allocate, special_name.IMPLICIT_CALL, pos);
      readonly_list<analyzable> ctor_params = make_list(parameters.elements);
      analyzable ctor_call = new parameter_analyzer(ctor_expression, ctor_params, pos);
      analyze_and_ignore_errors(ctor_call, pass);
    }

    return null;
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

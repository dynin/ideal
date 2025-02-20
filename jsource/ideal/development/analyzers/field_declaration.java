/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.actions.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

public class field_declaration extends single_pass_analyzer
    implements variable_declaration {

  private final annotation_set annotations;
  private final action_name name;
  private final variable_category category;
  private final type_flavor reference_flavor;
  private final type_flavor declared_in_flavor;
  private final type var_type;
  private variable_action variable_action;

  public field_declaration(annotation_set annotations, action_name name,
      variable_category category, type_flavor declared_in_flavor,
      type_flavor reference_flavor, type var_type, origin the_origin) {
    super(the_origin);
    this.annotations = annotations;
    this.name = name;
    this.category = category;
    this.declared_in_flavor = declared_in_flavor;
    this.reference_flavor = reference_flavor;
    this.var_type = var_type;

    assert category == variable_category.INSTANCE ||
           category == variable_category.STATIC;
  }

  @Override
  public action_name short_name() {
    return name;
  }

  @Override
  public annotation_set annotations() {
    return annotations;
  }

  @Override
  public variable_category get_category() {
    return category;
  }

  @Override
  public principal_type declared_in_type() {
    principal_type the_type = parent();

    while (the_type.short_name() == INSIDE_NAME) {
      the_type = the_type.get_parent();
    }

    return the_type;
  }

  @Override
  public boolean has_errors() {
    return false;
  }

  @Override
  public field_declaration specialize(specialization_context context,
      principal_type new_parent) {
    return this;
  }

  @Override
  public type_flavor get_flavor() {
    return declared_in_flavor;
  }

  @Override
  public type value_type() {
    return var_type;
  }

  @Override
  public type reference_type() {
    return common_types.get_reference(reference_flavor, var_type);
  }

  @Override
  public boolean declared_as_reference() {
    return false;
  }

  // TODO: override enum fields
  @Override
  public readonly_list<declaration> get_overriden() {
    return new empty<declaration>();
  }

  @Override
  public @Nullable action init_action() {
    return null;
  }

  public variable_action get_access() {
    if (variable_action == null) {
      if (category == variable_category.INSTANCE) {
        variable_action = new instance_variable(this, reference_flavor);
      } else {
        assert category == variable_category.STATIC;
        variable_action = new static_variable(this, reference_flavor);
      }
    }
    return variable_action;
  }

  @Override
  public readonly_list<analyzable> children() {
    // TODO: handle initializer?
    return new base_list<analyzable>(annotations);
  }

  @Override
  public analysis_result do_single_pass_analysis() {
    origin the_origin = this;

    get_context().add(declared_in_type().get_flavored(declared_in_flavor), name,
        get_access().to_action(the_origin));

    return common_values.nothing(the_origin);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}

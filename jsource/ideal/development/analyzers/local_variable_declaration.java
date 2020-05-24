/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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

public class local_variable_declaration extends single_pass_analyzer
    implements variable_declaration {

  private final annotation_set annotations;
  private final action_name name;
  private final type_flavor reference_flavor;
  private final type var_type;
  private final @Nullable action init_action;
  private final local_variable variable_action;
  private boolean has_been_added;

  // TODO: drop context
  public local_variable_declaration(annotation_set annotations, action_name name,
      type_flavor reference_flavor, type var_type, @Nullable action init_action, origin source) {
    super(source);
    this.annotations = annotations;
    this.name = name;
    this.reference_flavor = reference_flavor;
    this.var_type = var_type;
    this.init_action = init_action;
    this.variable_action = new local_variable(this, reference_flavor);
    this.has_been_added = false;
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
    return variable_category.LOCAL;
  }

  @Override
  public boolean has_errors() {
    return false;
  }

  @Override
  public local_variable_declaration specialize(specialization_context context,
      principal_type new_parent) {
    @Nullable principal_type parent_type = (principal_type) context.lookup(this.declared_in_type());
    if (parent_type == null) {
      parent_type = this.declared_in_type();
    }

    @Nullable principal_type variable_principal = var_type.principal();
    type var_type;
    if (variable_principal != null) {
      var_type = variable_principal.get_flavored(this.var_type.get_flavor());
    } else {
      var_type = this.var_type;
    }

    local_variable_declaration result = new local_variable_declaration(annotations, name,
        reference_flavor, var_type, init_action, this);
    result.set_context(parent_type, get_context());
    return result;
  }

  @Override
  public type value_type() {
    return var_type;
  }

  @Override
  public type reference_type() {
    return common_library.get_instance().get_reference(reference_flavor, var_type);
  }

  @Override
  public @Nullable action get_init() {
    return init_action;
  }

  // TODO: Break circular dependency.
  public local_variable get_access() {
    return variable_action;
  }

  @Override
  public action do_single_pass_analysis() {
    assert variable_action != null;
    origin the_origin = this;

    get_context().add(declared_in_type(), name, variable_action.to_action(the_origin));

    if (init_action == null) {
      return common_library.get_instance().void_instance().to_action(the_origin);
    } else {
      return new variable_initializer(variable_action, init_action);
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}

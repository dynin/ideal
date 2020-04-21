/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

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
import ideal.development.values.*;
import ideal.development.declarations.*;

public class local_variable_declaration extends base_action implements variable_declaration,
    analyzable {

  private final annotation_set annotations;
  private final action_name name;
  private final principal_type parent_type;
  private final type_flavor reference_flavor;
  private final type var_type;
  private final @Nullable action init_action;
  private final local_variable variable_action;

  // TODO: drop context
  public local_variable_declaration(annotation_set annotations, action_name name,
      principal_type parent_type, type_flavor reference_flavor, type var_type,
      @Nullable action init_action, position source) {
    super(source);
    this.annotations = annotations;
    this.name = name;
    this.parent_type = parent_type;
    this.reference_flavor = reference_flavor;
    this.var_type = var_type;
    this.init_action = init_action;
    this.variable_action = new local_variable(this, reference_flavor);
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
  public principal_type declared_in_type() {
    return parent_type;
  }

  @Override
  public local_variable_declaration specialize(specialization_context context,
      principal_type new_parent) {
    utilities.panic("local_variable_declaration.specialize() not implemented");
    return null;
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
  public action analyze() {
    assert variable_action != null;
    if (init_action == null) {
      return common_library.get_instance().void_instance().to_action(this);
    } else {
      return new variable_initializer(variable_action, init_action);
    }
  }

  // TODO: factor this out into variable_init_action.
  @Override
  public abstract_value result() {
    return common_library.get_instance().void_instance();
  }

  @Override
  public entity_wrapper execute(execution_context context) {
    if (init_action != null) {
      entity_wrapper result = init_action.execute(context);
      assert result instanceof value_wrapper;
      get_access().execute(context).init((value_wrapper) result);
      return result;
    } else {
      return common_library.do_get_undefined_instance();
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}

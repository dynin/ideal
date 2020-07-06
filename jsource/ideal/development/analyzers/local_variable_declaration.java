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
  private final @Nullable analyzable init_analyzable;
  private @Nullable action init_action;
  private local_variable variable_action;

  public local_variable_declaration(annotation_set annotations, action_name name,
      type_flavor reference_flavor, type var_type, analyzable init_analyzable, origin the_origin) {
    super(the_origin);
    this.annotations = annotations;
    this.name = name;
    this.reference_flavor = reference_flavor;
    this.var_type = var_type;
    this.init_analyzable = init_analyzable;
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
    type new_var_type;
    if (variable_principal != null) {
      new_var_type = variable_principal.get_flavored(this.var_type.get_flavor());
    } else {
      new_var_type = this.var_type;
    }

    // TODO: do we need to specialize init_analyzable?
    local_variable_declaration result = new local_variable_declaration(annotations, name,
        reference_flavor, new_var_type, init_analyzable, this);
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
  public boolean declared_as_reference() {
    return false;
  }

  @Override
  public @Nullable analyzable get_type_analyzable() {
    return analyzable_action.from(var_type, this);
  }

  @Override
  public @Nullable analyzable initializer() {
    return init_analyzable;
  }

  @Override
  public @Nullable action init_action() {
    if (init_analyzable != null) {
      assert init_action != null;
    }

    return init_action;
  }

  // TODO: Break circular dependency.
  public local_variable get_access() {
    if (variable_action == null) {
      variable_action = new local_variable(this, reference_flavor);
    }
    return variable_action;
  }

  public action dereference_access() {
    origin the_origin = this;
    return new dereference_action(var_type, null, the_origin).bind_from(get_access(), the_origin);
  }

  @Override
  public analysis_result do_single_pass_analysis() {
    origin the_origin = this;

    if (init_analyzable != null) {
      error_signal result = find_error(init_analyzable);
      if (result != null) {
        return result;
      }
      init_action = action_not_error(init_analyzable);
      if (!get_context().can_promote(init_action, var_type)) {
        return action_utilities.cant_promote(init_action.result(), var_type,
              get_context(), the_origin);
      }
      init_action = get_context().promote(init_action, var_type, the_origin);
    }

    get_context().add(declared_in_type(), name, get_access().to_action(the_origin));

    if (init_action == null) {
      return common_library.get_instance().void_instance().to_action(the_origin);
    } else {
      return new variable_initializer(get_access(), init_action);
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}

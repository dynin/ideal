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
import static ideal.development.flavors.flavor.*;
import ideal.development.declarations.*;

public class specialized_variable extends debuggable implements variable_declaration,
    analyzable {

  private final variable_analyzer main;
  private final principal_type parent_type;
  private final type value_type;
  private final type reference_type;
  private final @Nullable analyzable init;

  public specialized_variable(variable_analyzer main, principal_type parent_type,
      type value_type, type reference_type, @Nullable analyzable init) {
    this.main = main;
    this.parent_type = parent_type;
    this.value_type = value_type;
    this.reference_type = reference_type;
    this.init = init;

    assert get_category() == variable_category.LOCAL ||
           get_category() == variable_category.INSTANCE;
  }

  public variable_declaration get_main() {
    return main;
  }

  @Override
  public action_name short_name() {
    return main.short_name();
  }

  public action analyze() {
    return common_library.get_instance().void_instance().to_action(this);
  }

  public void add(analysis_context context) {
    if (get_category() == variable_category.LOCAL) {
      local_variable the_variable = new local_variable(this, reference_type.get_flavor());
      context.add(parent_type, short_name(), the_variable.to_action(this));
    } else if (get_category() == variable_category.INSTANCE) {
      analyzer_utilities.add_instance_variable(this, context);
    } else {
      utilities.panic("Unexpected var category");
    }
  }

  @Override
  public variable_category get_category() {
    return main.get_category();
  }

  @Override
  public annotation_set annotations() {
    return main.annotations();
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
  public specialized_variable specialize(specialization_context context,
      principal_type new_parent) {
    utilities.panic("specialized_variable.specialize() not implemented");
    return null;
  }

  @Override
  public origin deeper_origin() {
    return main;
  }

  @Override
  public type value_type() {
    return value_type;
  }

  @Override
  public type reference_type() {
    return reference_type;
  }

  @Override
  public @Nullable analyzable initializer() {
    return init;
  }

  @Override
  public @Nullable action init_action() {
    if (init == null) {
      return null;
    }

    return (action) init.analyze();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}

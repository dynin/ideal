/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

/**
 * Implements access to local variables.
 */
public class local_variable extends variable_action {

  public local_variable(variable_declaration the_declaration, type_flavor reference_flavor) {
    super(the_declaration, reference_flavor, the_declaration);
  }

  @Override
  protected variable_context get_context(entity_wrapper from_entity,
      execution_context the_context) {
    return action_utilities.get_context(the_context);
  }
}

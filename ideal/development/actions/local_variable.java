/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
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

  private local_variable(variable_declaration the_declaration, type_flavor reference_flavor,
      position source) {
    super(the_declaration, reference_flavor, null, source);
  }

  public local_variable(variable_declaration decl, type_flavor reference_flavor) {
    this(decl, reference_flavor, decl);
  }

  @Override
  protected variable_context get_context(execution_context the_context) {
    return action_utilities.get_context(the_context);
  }

  @Override
  protected variable_action make_action(variable_declaration the_declaration,
      @Nullable action from, position source) {
    return new local_variable(the_declaration, reference_flavor, source);
  }
}

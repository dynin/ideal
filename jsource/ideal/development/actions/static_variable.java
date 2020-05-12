/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

/**
 * Implements access to static variables.
 */
public class static_variable extends variable_action {

  private static_variable(variable_declaration the_declaration, type_flavor reference_flavor,
      origin source) {
    super(the_declaration, reference_flavor, null, source);
  }

  public static_variable(variable_declaration decl, type_flavor reference_flavor) {
    this(decl, reference_flavor, decl);
  }

  @Override
  protected variable_context get_context(execution_context the_context) {
    return action_utilities.get_context(the_context).static_context();
  }

  @Override
  protected variable_action make_action(variable_declaration the_declaration,
      @Nullable action from, origin source) {
    return new static_variable(the_declaration, reference_flavor, source);
  }
}

/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

/**
 * Implements access to instance variables.
 */
public class instance_variable extends variable_action {

  instance_variable(variable_declaration the_declaration, type_flavor reference_flavor,
      @Nullable action from, origin source) {
    super(the_declaration, reference_flavor, from, source);
  }

  public instance_variable(variable_declaration the_declaration, type_flavor reference_flavor) {
    this(the_declaration, reference_flavor, null, the_declaration);
  }

  @Override
  protected variable_context get_context(execution_context context) {
    assert from != null;
    entity_wrapper the_value = from.execute(context);
    if (!(the_value instanceof composite_wrapper)) {
      // TODO: use list_wrapper here explicitly
      assert the_value instanceof value_wrapper;
      assert the_declaration.short_name() == common_library.size_name;
      readonly_list the_list = (readonly_list) ((value_wrapper) the_value).unwrap();
      return new list_context(the_list);
    }
    assert the_value instanceof composite_wrapper;
    return (composite_wrapper) the_value;
  }

  @Override
  protected variable_action make_action(variable_declaration the_declaration,
      @Nullable action from, origin source) {
    return new instance_variable(the_declaration, reference_flavor, from, source);
  }
}

/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.declarations.*;
import ideal.development.types.*;
import ideal.development.flags.*;

import javax.annotation.Nullable;

/**
 * Implements access to instance variables.
 */
public class instance_variable extends variable_action implements chainable_action {

  public instance_variable(variable_declaration the_declaration, type_flavor reference_flavor) {
    super(the_declaration, reference_flavor, the_declaration);
  }

  @Override
  public final action combine(action from, origin the_origin) {
    return new chain_action(from, this, the_origin);
  }

  @Override
  protected variable_context get_context(entity_wrapper from_entity, execution_context context) {
    if (!(from_entity instanceof composite_wrapper)) {
      // TODO: use list_wrapper here explicitly
      assert from_entity instanceof value_wrapper;
      assert the_declaration.short_name() == common_names.size_name;
      readonly_list the_list = (readonly_list) ((value_wrapper) from_entity).unwrap();
      return new list_context(the_list);
    }
    assert from_entity instanceof composite_wrapper;
    return (composite_wrapper) from_entity;
  }
}

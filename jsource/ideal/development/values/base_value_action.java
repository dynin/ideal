/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

public class base_value_action<T extends entity_wrapper> extends debuggable
    implements action {

  private final origin the_origin;
  public final T the_value;

  public base_value_action(T the_value, origin the_origin) {
    assert the_origin != null;
    this.the_origin = the_origin;
    this.the_value = the_value;
  }

  @Override
  public final origin deeper_origin() {
    return the_origin;
  }

  @Override
  public abstract_value result() {
    return (type) the_value.type_bound();
  }

  @Override
  public boolean has_side_effects() {
    return false;
  }

  @Override
  public final action combine(action from, origin the_origin) {
    if (the_value instanceof procedure_value) {
      procedure_value the_procedure_value = (procedure_value) the_value;
      return the_procedure_value.bind_this_action(from, the_origin);
    } else {
      return this;
    }
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    return the_value;
  }

  @Override
  public @Nullable declaration get_declaration() {
    return null;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_value);
  }
}

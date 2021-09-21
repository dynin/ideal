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
import ideal.development.actions.*;

import javax.annotation.Nullable;

public class base_value_action<T extends value_wrapper> extends base_action {

  public final T the_value;

  public base_value_action(T the_value, origin source) {
    super(source);
    this.the_value = the_value;
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
  public value_wrapper execute(entity_wrapper from_entity, execution_context context) {
    return the_value;
  }

  @Override
  public action bind_from(action from, origin pos) {
    return this;
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

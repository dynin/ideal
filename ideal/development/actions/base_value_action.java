/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

public class base_value_action<T extends value_wrapper> extends base_action {

  public final T the_value;

  public base_value_action(T the_value, position source) {
    super(source);
    this.the_value = the_value;
  }

  @Override
  public abstract_value result() {
    return (type) the_value.type_bound();
  }

  @Override
  public value_wrapper execute(execution_context context) {
    return the_value;
  }

  @Override
  public action bind_from(action from, position pos) {
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

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
import ideal.development.declarations.*;
import ideal.development.names.*;

import javax.annotation.Nullable;

public abstract class base_data_value<T> extends debuggable
    implements abstract_value, value_wrapper<T>, stringable {

  private type bound;

  public base_data_value(type bound) {
    this.bound = bound;
  }

  @Override
  public type type_bound() {
    return bound;
  }

  @Override
  public T unwrap() {
    return (T) this;
  }

  @Override
  public action to_action(origin pos) {
    return new value_action(this, pos);
  }

  @Override
  public boolean is_parametrizable() {
    return bound.is_parametrizable();
  }

  public base_data_value bind_value(action from, origin pos) {
    return this;
  }

  public @Nullable declaration get_declaration() {
    return null;
  }
}

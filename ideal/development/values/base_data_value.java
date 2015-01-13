/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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

public class base_data_value<T> extends debuggable
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
  public final action to_action(position pos) {
    return new value_action(this, pos);
  }

  public T bind_from(action from, position pos) {
    return (T) this;
  }

  public @Nullable declaration get_declaration() {
    return null;
  }
}

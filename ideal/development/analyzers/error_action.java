/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.panic_value;

import javax.annotation.Nullable;

public class error_action extends error_signal implements action, abstract_value {

  public error_action(error_signal signal) {
    super(signal.cause, signal.is_cascading);
  }

  @Override
  public type type_bound() {
    return core_types.error_type();
  }

  @Override
  public action to_action(position pos) {
    return this;
  }

  @Override
  public abstract_value result() {
    return this;
  }

  @Override
  public @Nullable declaration get_declaration() {
    return null;
  }

  @Override
  public entity_wrapper execute(execution_context context) {
    return new panic_value(new base_string("Attempting to execute error_signal"));
  }

  @Override
  public action bind_from(action from, position pos) {
    return this;
  }
}

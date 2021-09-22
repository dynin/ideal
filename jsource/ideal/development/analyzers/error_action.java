/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.jumps.panic_value;

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
  public action to_action(origin pos) {
    return this;
  }

  @Override
  public abstract_value result() {
    return this;
  }

  @Override
  public boolean is_parametrizable() {
    return false;
  }

  @Override
  public @Nullable declaration get_declaration() {
    return null;
  }

  @Override
  public boolean has_side_effects() {
    return false;
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    return new panic_value(new base_string("Attempting to execute error_signal"));
  }

  @Override
  public action bind_from(action from, origin pos) {
    return this;
  }
}

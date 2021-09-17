/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.actions.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

public class value_action<T extends base_data_value> extends base_value_action<T> {

  public value_action(T the_value, origin source) {
    super(the_value, source);
  }

  @Override
  public abstract_value result() {
    return the_value;
  }

  @Override
  public final action bind_from(action from, origin the_origin) {
    return the_value.bind_value(from, the_origin).to_action(the_origin);
  }

  @Override
  public @Nullable declaration get_declaration() {
    return the_value.get_declaration();
  }
}

/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public abstract class type_action extends debuggable implements action {

  private final position source;

  protected type_action(position source) {
    assert source != null;
    this.source = source;
  }

  public abstract type get_type();

  @Override
  public abstract_value result() {
    return get_type();
  }

  @Override
  public final position source_position() {
    return source;
  }

  @Override
  public action bind_from(action from, position pos) {
    if (pos == source) {
      return this;
    } else {
      return new concrete_type_action(get_type(), pos);
    }
  }

  @Override
  @Nullable public declaration get_declaration() {
    return get_type().principal().get_declaration();
  }

  @Override
  public entity_wrapper execute(execution_context context) {
    return new typeinfo_value(get_type());
  }

  @Override
  public string to_string() {
    return new base_string("type-action: ", get_type().to_string());
  }
}

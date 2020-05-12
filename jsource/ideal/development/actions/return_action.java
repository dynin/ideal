/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;

public class return_action extends base_action {
  public final action expression;

  public return_action(action expression, origin source) {
    super(source);
    this.expression = expression;
  }

  @Override
  public abstract_value result() {
    return core_types.unreachable_type();
  }

  @Override
  public entity_wrapper execute(execution_context exec_context) {
    entity_wrapper result = expression.execute(exec_context);

    assert !(result instanceof error_signal);

    if (result instanceof jump_wrapper) {
      return result;
    } else {
      return new returned_value(result);
    }
  }

  @Override
  public string to_string() {
    return new base_string("return: ", expression.to_string());
  }
}

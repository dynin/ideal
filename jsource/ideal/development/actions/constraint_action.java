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
import ideal.development.declarations.*;
import ideal.development.values.*;

public class constraint_action extends base_action {
  public final action expression;

  public constraint_action(action expression, origin the_origin) {
    super(the_origin);
    this.expression = expression;
    assert expression != null;
  }

  @Override
  public abstract_value result() {
    return common_library.get_instance().immutable_void_type();
  }

  @Override
  public entity_wrapper execute(execution_context exec_context) {
    entity_wrapper expression_value = expression.execute(exec_context);
    common_library library = common_library.get_instance();
    // TODO: handle jumps
    if (expression_value == library.true_value()) {
      return library.void_instance();
    } else if (expression_value == library.false_value()) {
      return new panic_value(new base_string("Assertion failure"));
    } else {
      return new panic_value(new base_string("Neither true nor false in a conditional"));
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, expression);
  }
}

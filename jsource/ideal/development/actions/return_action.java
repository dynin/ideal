/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.jumps.*;
import ideal.development.values.*;

public class return_action extends base_action {
  public final action expression;
  public procedure_declaration the_procedure;
  public type return_type;

  public return_action(action expression, procedure_declaration the_procedure,
      type return_type, origin source) {
    super(source);
    this.expression = expression;
    this.the_procedure = the_procedure;
    this.return_type = return_type;
    assert expression != null;
    assert the_procedure != null;
    assert return_type != null;
  }

  @Override
  public abstract_value result() {
    return core_types.unreachable_type();
  }

  @Override
  public boolean has_side_effects() {
    return expression.has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context exec_context) {
    entity_wrapper result = expression.execute(null_wrapper.instance, exec_context);

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

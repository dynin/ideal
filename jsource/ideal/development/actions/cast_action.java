/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.jumps.*;
import ideal.development.values.*;
import ideal.development.flavors.*;

public class cast_action extends base_action {
  public final action expression;
  public final type the_type;
  public final cast_type the_cast_type;

  public cast_action(action expression, type the_type, cast_type the_cast_type, origin source) {
    super(source);
    this.expression = expression;
    this.the_type = the_type;
    this.the_cast_type = the_cast_type;
    if (the_type != common_types.immutable_void_type()) {
      assert the_type.is_subtype_of(common_types.value_type().get_flavored(flavor.any_flavor));
    }
  }

  @Override
  public abstract_value result() {
    return the_type;
  }

  @Override
  public boolean has_side_effects() {
    return expression.has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context the_context) {
    // TODO: handle jumps
    entity_wrapper expression_result = expression.execute(null_wrapper.instance, the_context);
    assert expression_result instanceof value_wrapper;

    if (the_type == common_types.immutable_void_type()) {
      return common_values.void_instance();
    }

    if (action_utilities.is_of(expression_result, the_type)) {
      return expression_result;
    } else {
      return new panic_value(new base_string("Can't cast " + expression_result.type_bound() +
          " to " + the_type));
    }
  }

  @Override
  public string to_string() {
    return new base_string("cast " + expression + " to " + the_type);
  }
}

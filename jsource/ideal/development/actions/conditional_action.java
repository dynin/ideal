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
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;

public class conditional_action extends base_action {
  public final action condition;
  public final action then_action;
  public final action else_action;
  private final abstract_value result;

  public conditional_action(action condition, action then_action, action else_action,
      abstract_value result, origin the_origin) {
    super(the_origin);
    this.condition = condition;
    this.then_action = then_action;
    this.else_action = else_action;
    this.result = result;

    assert action_utilities.is_result(condition,
        common_library.get_instance().immutable_boolean_type());
    assert action_utilities.is_result(then_action, result);
    assert action_utilities.is_result(else_action, result);
  }

  @Override
  public abstract_value result() {
    return result;
  }


  @Override
  public boolean has_side_effects() {
    return condition.has_side_effects() ||
           then_action.has_side_effects() ||
           else_action.has_side_effects();
  }

  @Override
  public entity_wrapper execute(execution_context the_context) {
    // TODO: handle jumps
    entity_wrapper condition_value = condition.execute(the_context);
    if (condition_value == common_library.get_instance().true_value()) {
      return then_action.execute(the_context);
    } else if (condition_value == common_library.get_instance().false_value()) {
      return else_action.execute(the_context);
    } else {
      return new panic_value(new base_string("Neither true nor false in a conditional"));
    }
  }
}

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
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;

/**
 * Create an action out of a sequence (list) of actions.
 */
public class list_action extends base_action {
  private final readonly_list<action> subactions;

  public list_action(readonly_list<action> subactions, origin source) {
    super(source);
    assert subactions != null;
    this.subactions = subactions;
  }

  public readonly_list<action> elements() {
    return subactions;
  }

  // TODO: cache
  @Override
  public abstract_value result() {
    abstract_value result = common_library.get_instance().void_instance();

    for (int i = 0; i < subactions.size(); ++i) {
      result = subactions.get(i).result();
      // TODO: this should never happen.
      assert ! (result instanceof error_signal);
      if (result.type_bound() == core_types.unreachable_type()) {
        return result;
      }
    }

    return result;
  }

  @Override
  public entity_wrapper execute(execution_context exec_context) {
    entity_wrapper result = common_library.get_instance().void_instance();

    for (int i = 0; i < subactions.size(); ++i) {
      // TODO: handle early returns, etc.
      result = subactions.get(i).execute(exec_context);
      if (result.type_bound() == core_types.unreachable_type()) {
        return result;
      }
    }

    return result;
  }
}

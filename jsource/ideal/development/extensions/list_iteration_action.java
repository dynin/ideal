/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.extensions.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.declarations.*;
import ideal.development.analyzers.*;

public class list_iteration_action extends base_action<list_iteration_analyzer> {
  list_iteration_action(list_iteration_analyzer source) {
    super(source);
  }

  @Override
  public abstract_value result() {
    return common_library.get_instance().void_type();
  }

  @Override
  public entity_wrapper execute(execution_context exec_context) {
    entity_wrapper init_entity = source.init_action.execute(exec_context);
    if (init_entity instanceof error_signal) {
      return init_entity;
    }

    if (! (init_entity instanceof value_wrapper)) {
      return new panic_value(new base_string("list value expected, got " +
          init_entity.type_bound()));
    }

    readonly_value init_value = (readonly_value) ((value_wrapper) init_entity).unwrap();

    if (! (init_value instanceof readonly_list)) {
      return new panic_value(new base_string("list expected, got " +
          init_entity.type_bound()));
    }

    readonly_list<value_wrapper> values = (readonly_list) init_value;

    for (int i = 0; i < values.size(); ++i) {
      entity_wrapper element = values.get(i);
      // TODO: for now, we are just skipping missing values.
      // We should be smarter about this...
      if (element == null) {
        continue;
      }
      assert element instanceof value_wrapper;
      source.element_var.execute(exec_context).set((value_wrapper) element);
      entity_wrapper body_result = source.body_action().execute(exec_context);
      // TODO: check if return was used or exception was thrown.
    }

    // TODO: fix.
    return common_library.get_instance().void_instance();
  }
}

/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class loop_action extends base_action {
  private @Nullable action body;
  private boolean has_breaks;

  public loop_action(@Nullable action body, position source) {
    super(source);
    this.body = body;
    this.has_breaks = false;
  }

  public loop_action(position source) {
    this(null, source);
  }

  public void set_body(action body) {
    assert this.body == null;
    assert body != null;
    this.body = body;
  }

  public void use_break() {
    has_breaks = true;
  }

  @Override
  public abstract_value result() {
    if (has_breaks) {
      return common_library.get_instance().immutable_void_type();
    } else {
      return core_types.unreachable_type();
    }
  }

  @Override
  public entity_wrapper execute(execution_context exec_context) {
    assert body != null;
    while (true) {
      entity_wrapper result = body.execute(exec_context);
      assert !(result instanceof error_signal);

      if (result instanceof jump_wrapper) {
        if (result instanceof loop_jump_wrapper) {
          loop_jump_wrapper the_loop_jump = (loop_jump_wrapper) result;
          if (the_loop_jump.the_loop == this) {
            switch (the_loop_jump.the_jump_type) {
              case BREAK_JUMP:
                // Yay! I get a break!
                return common_library.get_instance().void_instance();
              case CONTINUE_JUMP:
                continue;
            }
          }
        }
        return result;
      }
    }
  }

  @Override
  public string to_string() {
    return new base_string("loop: ", body.to_string());
  }
}

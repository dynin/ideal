/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.constructs.*;
import ideal.development.values.*;

public class loop_jump_wrapper extends jump_wrapper {

  public final jump_category the_jump_category;
  public final loop_action the_loop;

  public loop_jump_wrapper(jump_category the_jump_category, loop_action the_loop) {
    this.the_jump_category = the_jump_category;
    this.the_loop = the_loop;
  }

  @Override
  public string to_string() {
    return new base_string(new base_string("jump: "), the_jump_category.to_string(),
        the_loop.to_string());
  }
}

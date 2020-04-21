/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class jump_analyzer extends single_pass_analyzer {

  public final jump_type the_jump_type;

  public jump_analyzer(jump_type the_jump_type, position pos) {
    super(pos);
    this.the_jump_type = the_jump_type;
  }

  public jump_analyzer(jump_construct the_jump) {
    super(the_jump);
    the_jump_type = the_jump.the_jump_type;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {

    @Nullable loop_action the_loop = analyzer_utilities.get_enclosing_loop(this);

    if (the_loop == null) {
      return new error_signal(messages.jump_outside_loop, this);
    }

    if (the_jump_type == jump_type.BREAK_JUMP) {
      the_loop.use_break();
    } else if (the_jump_type == jump_type.CONTINUE_JUMP) {
      // TODO: propagate constraints?
    }

    return new base_value_action(new loop_jump_wrapper(the_jump_type, the_loop), this);
  }
}

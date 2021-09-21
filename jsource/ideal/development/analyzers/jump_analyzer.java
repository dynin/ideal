/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.jumps.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.values.*;

public class jump_analyzer extends single_pass_analyzer {

  public final jump_category the_jump_category;

  public jump_analyzer(jump_category the_jump_category, origin pos) {
    super(pos);
    this.the_jump_category = the_jump_category;
  }

  public jump_analyzer(jump_construct the_jump) {
    super(the_jump);
    the_jump_category = the_jump.the_jump_category;
  }

  @Override
  public readonly_list<analyzable> children() {
    return new empty<analyzable>();
  }

  @Override
  protected analysis_result do_single_pass_analysis() {

    @Nullable loop_action the_loop = analyzer_utilities.get_enclosing_loop(this);

    if (the_loop == null) {
      return new error_signal(messages.jump_outside_loop, this);
    }

    if (the_jump_category == jump_category.BREAK_JUMP) {
      the_loop.use_break();
    } else if (the_jump_category == jump_category.CONTINUE_JUMP) {
      // TODO: propagate constraints?
    }

    return new base_value_action(new loop_jump_wrapper(the_jump_category, the_loop), this);
  }
}

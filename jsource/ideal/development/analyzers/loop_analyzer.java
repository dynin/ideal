/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;

public class loop_analyzer extends single_pass_analyzer implements declaration {

  private static final special_name LOOP_NAME =
      new special_name(new base_string("loop"), new base_string("loop_analyzer"));

  public final analyzable body;
  private principal_type inside;
  private @Nullable loop_action the_loop_action;

  public loop_analyzer(analyzable body, origin pos) {
    super(pos);
    this.body = body;
  }

  public loop_analyzer(loop_construct source) {
    super(source);
    body = make(source.body);
  }

  public loop_action get_loop_action() {
    assert the_loop_action != null;
    return the_loop_action;
  }

  @Override
  public principal_type inner_type() {
    return inside;
  }

  @Override
  public readonly_list<analyzable> children() {
    return new base_list<analyzable>(body);
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    if (inside == null) {
      inside = make_block(LOOP_NAME, this);
    }

    the_loop_action = new loop_action(this);

    if (find_error(body) != null) {
      return new error_signal(messages.error_in_block, body, this);
    }

    the_loop_action.set_body(action_not_error(body));

    return the_loop_action;
  }
}

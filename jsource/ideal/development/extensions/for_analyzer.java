/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.names.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

public class for_analyzer extends extension_analyzer {
  public analyzable init;
  public analyzable condition;
  public analyzable update;
  public analyzable body;

  public for_analyzer(analyzable init, analyzable condition, analyzable update, analyzable body,
      origin the_origin) {
    super(the_origin);
    this.init = init;
    this.condition = condition;
    this.update = update;
    this.body = body;
  }

  @Override
  public analyzable do_expand() {
    origin the_origin = this;

    analyzable body_and_update = new statement_list_analyzer(
        new base_list<analyzable>(body, update), the_origin);
    analyzable break_statement = new jump_analyzer(jump_type.BREAK_JUMP, the_origin);
    analyzable if_statement = new conditional_analyzer(condition,
        body_and_update, break_statement, the_origin);
    analyzable loop_statement = new loop_analyzer(if_statement, the_origin);
    return new block_analyzer(new statement_list_analyzer(
        new base_list<analyzable>(init, loop_statement), the_origin), the_origin);
  }
}

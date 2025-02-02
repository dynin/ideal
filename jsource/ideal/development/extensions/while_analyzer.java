/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.jumps.*;
import ideal.development.constructs.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

public class while_analyzer extends extension_analyzer {
  public analyzable condition;
  public analyzable body;

  public while_analyzer(analyzable condition, analyzable body, origin the_origin) {
    super(the_origin);
    this.condition = condition;
    this.body = body;
  }

  @Override
  public analyzable do_expand() {
    origin the_origin = this;

    analyzable break_statement = new jump_analyzer(jump_category.BREAK_JUMP, the_origin);
    analyzable if_statement = new conditional_analyzer(condition,
        body, break_statement, the_origin);
    return new loop_analyzer(if_statement, the_origin);
  }
}

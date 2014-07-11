/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class return_construct extends base_construct {
  public final @Nullable construct the_expression;

  public return_construct(@Nullable construct the_expression, position pos) {
    super(pos);
    this.the_expression = the_expression;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    if (the_expression != null) {
      result.append(the_expression);
    }

    return result;
  }
}

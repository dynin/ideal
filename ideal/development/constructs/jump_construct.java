/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class jump_construct extends base_construct {
  public final jump_type the_jump_type;

  public jump_construct(jump_type the_jump_type, position pos) {
    super(pos);
    this.the_jump_type = the_jump_type;
  }

  @Override
  public readonly_list<construct> children() {
    return new empty<construct>();
  }
}

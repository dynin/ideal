/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class literal_construct extends base_construct {
  public literal the_literal;
  public literal_construct(literal the_literal, origin pos) {
    super(pos);
    this.the_literal = the_literal;
  }

  public readonly_list<construct> children() {
    return new empty<construct>();
  }
}

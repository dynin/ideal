/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class flavor_construct extends base_construct {
  public final type_flavor flavor;
  public final construct expr;
  public flavor_construct(type_flavor flavor,
                          construct expr,
                          origin pos) {
    super(pos);
    this.flavor = flavor;
    this.expr = expr;
  }

  public readonly_list<construct> children() {
    return new base_list<construct>(expr);
  }
}

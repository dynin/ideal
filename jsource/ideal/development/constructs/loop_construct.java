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

public class loop_construct extends base_construct {
  public final construct body;

  public loop_construct(construct body, origin pos) {
    super(pos);
    this.body = body;
  }

  @Override
  public readonly_list<construct> children() {
    return new base_list<construct>(body);
  }
}

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

public class modifier_construct extends base_construct
    implements annotation_construct {

  public final modifier_kind the_kind;

  public modifier_construct(modifier_kind the_kind, origin pos) {
    super(pos);
    assert the_kind != null;
    this.the_kind = the_kind;
  }

  public readonly_list<construct> children() {
    return new empty<construct>();
  }
}

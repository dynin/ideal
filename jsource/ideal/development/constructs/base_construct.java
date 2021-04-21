/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public abstract class base_construct extends debuggable implements construct {

  @dont_display
  private final origin the_origin;

  public abstract readonly_list<construct> children();

  public base_construct(origin the_origin) {
    assert the_origin != null;
    this.the_origin = the_origin;
  }

  public origin deeper_origin() {
    return the_origin;
  }

  // TODO: this is a hack to work around Java's type system...
  protected void do_append_all(list<construct> target, readonly_list<? extends construct> source) {
    target.append_all((readonly_list<construct>) source);
  }
}

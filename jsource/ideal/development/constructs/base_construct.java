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
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public abstract class base_construct extends debuggable implements construct {

  @dont_display
  private final origin pos;

  public abstract readonly_list<construct> children();

  public base_construct(origin pos) {
    assert pos != null;
    this.pos = pos;
  }

  public origin deeper_origin() {
    return pos;
  }

  public boolean has(predicate<construct> the_predicate) {
    if (the_predicate.call(this)) {
      return true;
    }

    readonly_list<construct> the_children = children();
    for (int i = 0; i < the_children.size(); ++i) {
      if (the_children.get(i).has(the_predicate)) {
        return true;
      }
    }

    return false;
  }

  // TODO: this is a hack to work around Java's type system...
  protected void do_append_all(list<construct> target, readonly_list<? extends construct> source) {
    target.append_all((readonly_list<construct>) source);
  }
}

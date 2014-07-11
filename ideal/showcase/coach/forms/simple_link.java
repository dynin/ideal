/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
public class simple_link implements widget {
  public final string name;
  public final string href;

  public simple_link(string name, string href) {
    this.name = name;
    this.href = href;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_simple_link(this);
  }
}

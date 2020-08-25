/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
public class link implements widget {
  public final String name;
  public final procedure0arg target;

  public link(String name, procedure0arg target) {
    this.name = name;
    this.target = target;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_link(this);
  }
}

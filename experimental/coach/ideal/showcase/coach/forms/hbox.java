/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
public class hbox implements widget {
  public final readonly_list<widget> columns;

  public hbox(readonly_list<widget> columns) {
    this.columns = columns;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_hbox(this);
  }
}

/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
public class label implements widget {
  public final String content;

  public label(String content) {
    this.content = content;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_label(this);
  }
}

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

/**
 * TODO: replace with a styled label.
 */
public class bold implements widget {
  public final String content;

  public bold(String content) {
    this.content = content;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_bold(this);
  }
}

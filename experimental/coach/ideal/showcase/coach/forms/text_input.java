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
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
public class text_input implements widget {
  public final reference_wrapper<string> model;

  public text_input(reference_wrapper<string> model) {
    this.model = model;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_text_input(this);
  }
}

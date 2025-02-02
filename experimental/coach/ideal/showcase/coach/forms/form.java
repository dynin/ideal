/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
public class form implements widget {
  // TODO: make method an argument to form.
  public static final String FORM_METHOD = "POST";

  public final widget content;
  public final string action;

  public form(widget content, string action) {
    this.content = content;
    this.action = action;
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_form(this);
  }
}

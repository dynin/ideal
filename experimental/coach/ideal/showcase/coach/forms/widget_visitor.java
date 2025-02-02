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
public abstract class widget_visitor<R> {
  public R visit(widget the_widget) {
    return the_widget.accept(this);
  }

  public abstract R visit_label(label the_label);
  public abstract R visit_html_text(html_text the_html_text);
  public abstract R visit_bold(bold the_bold);
  public abstract R visit_javascript(javascript the_javascript);
  public abstract R visit_div(div the_div);
  public abstract R visit_text_input(text_input the_text_input);
  public abstract R visit_raw_html(raw_html the_raw_html);
  public abstract R visit_textarea_input(textarea_input the_textarea_input);
  public abstract R visit_select_input(select_input the_select_input);
  public abstract R visit_form(form the_form);
  public abstract R visit_vbox(vbox the_vbox);
  public abstract R visit_hbox(hbox the_hbox);
  public abstract R visit_link(link the_link);
  public abstract R visit_simple_link(simple_link the_simple_link);
  public abstract R visit_button(button the_button);
}

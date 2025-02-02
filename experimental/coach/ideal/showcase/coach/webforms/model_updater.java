/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.webforms;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import ideal.runtime.channels.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.values.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.forms.*;
import ideal.showcase.coach.forms.select_input.option;

import java.io.*;
public class model_updater extends widget_visitor<Void> {

  private final datastore_state state;
  private final parameter_context context;
  private final input_id_generator id_generator;

  public model_updater(datastore_state state, parameter_context context) {
    this.state = state;
    this.context = context;
    this.id_generator = new input_id_generator();
  }

  public void update_model(widget the_widget) {
    visit(the_widget);
  }

  private string next_field_value() {
    return context.get(new name(id_generator.next_escaped_id()));
  }

  @Override
  public Void visit_label(label the_label) {
    return null;
  }

  @Override
  public Void visit_html_text(html_text the_html_text) {
    return null;
  }

  @Override
  public Void visit_raw_html(raw_html the_raw_html) {
    return null;
  }

  @Override
  public Void visit_bold(bold the_bold) {
    return null;
  }

  @Override
  public Void visit_javascript(javascript the_javascript) {
    return null;
  }

  @Override
  public Void visit_div(div the_div) {
    update_model(the_div.content);
    return null;
  }

  @Override
  public Void visit_text_input(text_input the_text_input) {
    reference_wrapper model = the_text_input.model;
    model.set(state.get_schema().new_string(next_field_value()));
    return null;
  }

  @Override
  public Void visit_textarea_input(textarea_input the_textarea_input) {
    reference_wrapper model = the_textarea_input.model;
    model.set(state.get_schema().new_string(next_field_value()));
    return null;
  }

  @Override
  public Void visit_select_input(select_input the_select_input) {
    reference_wrapper model = the_select_input.model;
    string field = next_field_value();
    int value;
    try {
      value = Integer.parseInt(utilities.s(field));
    } catch (Exception e) {
      log.debug("Non-integer value in form: " + field);
      return null;
    }
    if (value >= 0 && value < the_select_input.the_options.size()) {
      readonly_list<option> the_options = the_select_input.the_options;
      model.set(the_options.get(value).value);
    }

    return null;
  }

  @Override
  public Void visit_form(form the_form) {
    return null;
  }

  @Override
  public Void visit_vbox(vbox the_vbox) {
    readonly_list<widget> children = the_vbox.rows;
    for (int i = 0; i < children.size(); ++i) {
      update_model(children.get(i));
    }
    return null;
  }

  @Override
  public Void visit_hbox(hbox the_hbox) {
    readonly_list<widget> children = the_hbox.columns;
    for (int i = 0; i < children.size(); ++i) {
      update_model(children.get(i));
    }
    return null;
  }

  @Override
  public Void visit_link(link the_link) {
    return null;
  }

  @Override
  public Void visit_simple_link(simple_link the_simple_link) {
    return null;
  }

  @Override
  public Void visit_button(button the_button) {
    return null;
  }
}

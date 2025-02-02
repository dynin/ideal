/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.webforms;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.channels.*;
import ideal.machine.elements.*;
import ideal.machine.channels.*;
import ideal.development.elements.*;
import ideal.development.values.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.forms.*;
import ideal.showcase.coach.forms.select_input.option;

import java.io.*;
public class view_renderer extends widget_visitor<String> {

  private final render_context context;
  private final input_id_generator id_generator;

  public view_renderer(render_context context) {
    this.context = context;
    this.id_generator = new input_id_generator();
  }

  public String render(widget the_widget) {
    return visit(the_widget);
  }

  @Override
  public String visit_label(label the_label) {
    return escape_html(the_label.content);
  }

  @Override
  public String visit_html_text(html_text the_html_text) {
    string_writer the_writer = new string_writer();
    markup_formatter out = new markup_formatter(the_writer);
    out.write(the_html_text.the_text);
    return utilities.s(the_writer.elements());
  }

  @Override
  public String visit_raw_html(raw_html the_raw_html) {
    return utilities.s(the_raw_html.content);
  }

  @Override
  public String visit_bold(bold the_bold) {
    return "<b>" + escape_html(the_bold.content) + "</b>";
  }

  @Override
  public String visit_javascript(javascript the_javascript) {
    return "<script language='JavaScript'>\n" +
        "// <![CDATA[\n" +
        the_javascript.content + "\n" +
        "// ]]>\n" +
        "</script>\n";
  }

  @Override
  public String visit_div(div the_div) {
    return "<div class='" + escape_html(the_div.style) + "'>" + render(the_div.content) + "</div>";
  }

  @Override
  public String visit_text_input(text_input the_text_input) {
    return "<input type=\"text\" name=\"" + id_generator.next_escaped_id() +
        "\" value=\"" + escape_html(the_text_input.model.get()) + "\" />\n";
  }

  @Override
  public String visit_textarea_input(textarea_input the_textarea_input) {
    return "<textarea rows=\"30\" cols=\"80\" name=\"" +
        id_generator.next_escaped_id() + "\">" +
        escape_html(the_textarea_input.model.get()) + "</textarea>\n";
  }

  @Override
  public String visit_select_input(select_input the_select_input) {
    StringBuilder sb = new StringBuilder();
    sb.append("<select name=\"" + id_generator.next_escaped_id() + "\">\n");
    readonly_list<option> the_options = the_select_input.the_options;
    for (int i = 0; i < the_options.size(); ++i) {
      option opt = the_options.get(i);
      sb.append("<option value=\"" + i + "\"");
      if (runtime_util.values_equal(opt.value, the_select_input.model.get())) {
        sb.append(" selected=\"selected\"");
      }
      sb.append(">");
      sb.append(escape_html(opt.name));
      sb.append("</option>\n");
    }
    sb.append("</select>\n");
    return sb.toString();
  }

  @Override
  public String visit_form(form the_form) {
    StringBuilder sb = new StringBuilder();

    sb.append(render_form_start(the_form));
    sb.append(render(the_form.content));
    sb.append(render_form_end());

    return sb.toString();
  }

  public String render_form_start(form the_form) {
    return "<form action=\"" + escape_html(the_form.action) +
        "\" method=\"" + form.FORM_METHOD + "\">\n";
  }

  public String render_form_end() {
    return "</form>\n";
  }

  public String render_hidden_input(identifier the_name, string value) {
    return "<input type=\"hidden\" name=\"" + escape_html(the_name) +
        "\" value=\"" + escape_html(utilities.s(value)) + "\" />\n";
  }

  @Override
  public String visit_vbox(vbox the_vbox) {
    StringBuilder sb = new StringBuilder("<table>\n");
    readonly_list<widget> rows = the_vbox.rows;
    for (int i = 0; i < rows.size(); ++i) {
      widget row = rows.get(i);
      sb.append("<tr>");
      if (row instanceof hbox) {
        sb.append(render(row));
      } else {
        sb.append("<td>\n");
        sb.append(render(row));
        sb.append("</td>\n");
      }
      sb.append("</tr>\n");
    }
    sb.append("</table>\n");
    return sb.toString();
  }

  @Override
  public String visit_hbox(hbox the_hbox) {
    StringBuilder sb = new StringBuilder();
    readonly_list<widget> columns = the_hbox.columns;
    for (int i = 0; i < columns.size(); ++i) {
      widget column = columns.get(i);
      sb.append("<td>");
      sb.append(render(column));
      sb.append("</td>\n");
    }
    return sb.toString();
  }

  @Override
  public String visit_link(link the_link) {
    StringBuilder sb = new StringBuilder("<a href=\"");
    sb.append(escape_html(context.to_uri(the_link.target)));
    sb.append("\">");
    sb.append(escape_html(the_link.name));
    sb.append("</a>");

    return sb.toString();
  }

  @Override
  public String visit_simple_link(simple_link the_simple_link) {
    StringBuilder sb = new StringBuilder("<a href=\"");
    sb.append(escape_html(the_simple_link.href));
    sb.append("\">");
    sb.append(escape_html(the_simple_link.name));
    sb.append("</a>");

    return sb.toString();
  }

  @Override
  public String visit_button(button the_button) {
    return "<input type=\"submit\" name=\"" + escape_html(context.to_button_id(the_button.target)) +
        "\" value=\"" + escape_html(the_button.name) + "\" />\n";
  }

  private static String escape_html(identifier id) {
    return escape_html(id.to_string());
  }

  public static String escape_html(string s) {
    return utilities.s(runtime_util.escape_markup(s));
  }

  public static String escape_html(value_wrapper vw) {
    return escape_html((string) vw.unwrap());
  }

  public static String escape_html(String s) {
    return escape_html(new base_string(s));
  }
}

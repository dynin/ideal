/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
public class styles {

  public static final text_element indent_style = make_div(new base_string("indent"));

  public static final text_element documentation_style =
      make_div(new base_string("documentation"));

  public static final text_element documentation_note_style =
      make_div(new base_string("documentation-note"));

  public static final text_element type_declaration_style =
      make_div(new base_string("type-declaration"));

  public static final text_element type_declaration_name_style =
      make_span(new base_string("type-declaration-name"));

  public static final text_element supertype_declaration_name_style =
      make_span(new base_string("supertype-declaration-name"));

  public static final text_element var_declaration_name_style =
      make_span(new base_string("var-declaration-name"));

  public static final text_element procedure_declaration_style =
      make_div(new base_string("procedure-declaration"));

  public static final text_element procedure_declaration_name_style =
      make_span(new base_string("procedure-declaration-name"));

  public static final text_element code_style = make_span(new base_string("code"));

  public static final base_string nav_table_style = new base_string("nav-table");

  public static final base_string nav_left_style = new base_string("nav-left");

  public static final base_string nav_center_style = new base_string("nav-center");

  public static final base_string nav_right_style = new base_string("nav-right");

  public static text_fragment wrap(text_element element, @Nullable text_fragment fragment) {
    assert element.children() == null;
    return new base_element(element.get_id(), element.attributes(), fragment);
  }

  private static text_element make_div(base_string div_class) {
    return base_element.make(text_library.DIV, text_library.CLASS, div_class, null);
  }

  private static text_element make_span(base_string span_class) {
    return base_element.make(text_library.SPAN, text_library.CLASS, span_class, null);
  }
}

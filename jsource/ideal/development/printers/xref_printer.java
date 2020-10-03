/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.actions.*;
import ideal.development.declarations.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.comments.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.scanners.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.documenters.*;

/**
 * Render the cross-references page.
 */
public class xref_printer {

  private final analysis_context the_analysis_context;
  private final xref_context the_xref_context;
  private final naming_strategy the_naming_strategy;
  private final value_printer the_value_printer = new base_value_printer(null);

  public xref_printer(analysis_context the_analysis_context, xref_context the_xref_context,
      naming_strategy the_naming_strategy) {
    this.the_analysis_context = the_analysis_context;
    this.the_xref_context = the_xref_context;
    this.the_naming_strategy = the_naming_strategy;
  }

  public text_fragment print_statements(readonly_list<? extends construct> statements) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    for (int i = 0; i < statements.size(); ++i) {
      construct the_statement = statements.get(i);
      text_fragment printed = print(the_statement);
      if (printed != null) {
        fragments.append(printed);
      }
    }
    return text_util.join(fragments);
  }

  public @Nullable text_fragment print(construct c) {
    if (c instanceof type_declaration_construct) {
      return process_type_declaration((type_declaration_construct) c);
    }
    return null;
  }

  public text_fragment process_type_declaration(type_declaration_construct c) {
    analyzable a = the_analysis_context.get_analyzable(c);
    if (!(a instanceof type_declaration)) {
      return null;
    }

    type_declaration the_declaration = (type_declaration) a;
    text_fragment title = new base_string(c.kind.to_string(), " ",
        the_value_printer.print_value(the_declaration.get_declared_type()));
    text_fragment title_text = styles.wrap(styles.xref_title_style, title);

    origin target = the_xref_context.get_target(the_declaration, xref_mode.DECLARATION);
    text_fragment declaration_text = styles.wrap(styles.xref_links_style,
        text_util.join(new base_string("Declaration: "), get_link(target)));
    return text_util.join(title_text, declaration_text);
  }

  private text_fragment get_link(@Nullable origin target) {
    if (target == null) {
      return new base_string("[null]");
    }

    target = the_analysis_context.get_analyzable((construct) target);

    if (!(target instanceof type_declaration)) {
      return (base_string) target.to_string();
    }

    type_declaration the_declaration = (type_declaration) target;
    principal_type the_type = the_declaration.get_declared_type();
    // TODO: fail gracefully if name is not a simple_name
    text_fragment the_text =
        the_naming_strategy.print_simple_name((simple_name) the_type.short_name());
    @Nullable string link = the_naming_strategy.link_to_type(the_type, link_mode.STYLISH);
    if (link != null) {
      the_text = text_util.make_html_link(the_text, link);
    }
    return the_text;
  }
}

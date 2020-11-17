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
    if (the_declaration.has_errors()) {
      return null;
    }

    text_fragment title = new base_string(c.kind.to_string(), " ",
        the_value_printer.print_value(the_declaration.get_declared_type()));
    text_fragment title_text = styles.wrap(styles.xref_title_style, title);

    text_fragment declaration_text = get_links("Declaration", the_declaration,
        xref_mode.DECLARATION, true);
    text_fragment supertypes_text = get_links("Supertypes", the_declaration,
        xref_mode.SUPERTYPE_DECLARATION, true);
    text_fragment subtypes_text = get_links("Subtypes", the_declaration,
        xref_mode.SUPERTYPE_DECLARATION, false);

    return text_util.join(title_text, declaration_text, supertypes_text, subtypes_text);
  }

  private text_fragment get_links(String name, declaration the_declaration, xref_mode mode,
      boolean targets) {
    @Nullable readonly_list<origin> links;

    if (targets) {
      links = the_xref_context.get_targets(the_declaration, mode);
    } else {
      links = the_xref_context.get_sources(the_declaration, mode);
    }

    if (links == null || links.is_empty()) {
      return text_util.EMPTY_FRAGMENT;
    }

    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(new base_string(name, ": "));
    for (int i = 0; i < links.size(); ++i) {
      fragments.append(i == 0 ? new base_string(" ") : new base_string(" / "));
      origin link = links.get(i);
      @Nullable declaration link_declaration = the_xref_context.origin_to_declaration(link);
      if (link_declaration != null) {
        fragments.append(render_link(link_declaration));
      } else {
        fragments.append(new base_string("-" + link + "@" + link.getClass()));
      }
    }

    return styles.wrap(styles.xref_links_style, text_util.join(fragments));
  }

  private text_fragment render_link(declaration the_declaration) {
    if (!(the_declaration instanceof type_declaration)) {
      return new base_string("" + the_declaration);
    }

    type_declaration the_type_declaration = (type_declaration) the_declaration;
    principal_type the_type = the_type_declaration.get_declared_type();
    // TODO: fail gracefully if name is not a simple_name
    text_fragment the_text =
        the_naming_strategy.print_simple_name((simple_name) the_type.short_name());
    @Nullable string link = the_naming_strategy.link_to_declaration(the_type_declaration,
        link_mode.STYLISH);
    if (link != null) {
      the_text = text_util.make_html_link(the_text, link);
    }
    return the_text;
  }
}

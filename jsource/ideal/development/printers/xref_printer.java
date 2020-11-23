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
import ideal.development.flavors.*;
import ideal.development.literals.*;
import ideal.development.scanners.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.analyzers.*;
import ideal.development.values.*;
import ideal.development.documenters.*;

/**
 * Render the cross-references page.
 */
public class xref_printer {

  // TODO: factor out
  private final analysis_context the_analysis_context;
  private final xref_context the_xref_context;
  private final naming_strategy the_naming_strategy;
  // TODO: retire
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

    list<text_fragment> fragments = new base_list<text_fragment>();

    text_fragment title = new base_string(c.kind.to_string(), " ",
        the_value_printer.print_value(the_declaration.get_declared_type()));
    fragments.append(styles.wrap(styles.xref_title_style, title));

    fragments.append(get_links("Declaration", the_declaration, xref_mode.TYPE_DECLARATION, true));
    fragments.append(get_links("Direct supertypes", the_declaration, xref_mode.DIRECT_SUPERTYPE,
        true));
    fragments.append(get_links("All supertypes", the_declaration, xref_mode.INDIRECT_SUPERTYPE,
        true));
    fragments.append(get_links("Direct subtypes", the_declaration, xref_mode.DIRECT_SUPERTYPE,
        false));
    fragments.append(get_links("All subtypes", the_declaration, xref_mode.INDIRECT_SUPERTYPE,
        false));
    fragments.append(get_links("Use", the_declaration, xref_mode.USE, true));

    return text_util.join(fragments);
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
      origin the_origin = links.get(i);
      type_flavor the_flavor = null;
      if (the_origin instanceof flavor_construct) {
        the_flavor = ((flavor_construct) the_origin).flavor;
      } else if (the_origin instanceof type_action) {
        type the_type = ((type_action) the_origin).get_type();
        if (the_type.get_flavor() != flavor.nameonly_flavor) {
          the_flavor = the_type.get_flavor();
        }
      }
      if (the_flavor != null) {
        fragments.append(new base_string(the_flavor.name().to_string(), " "));
      }
      fragments.append(render_origin(the_origin));
    }

    return styles.wrap(styles.xref_links_style, text_util.join(fragments));
  }

  private text_fragment render_origin(origin the_origin) {
    @Nullable name_construct the_name_construct = printer_util.unwrap_name(the_origin);
    if (the_name_construct != null) {
      return render_name(the_name_construct);
    } else {
      return render_declaration(the_origin);
    }
  }

  private text_fragment render_name(name_construct the_name_construct) {
    principal_type the_type = the_xref_context.get_parent_type(the_name_construct);
    text_fragment the_text;
    if (the_type == the_naming_strategy.get_current_type()) {
      the_text = print_name(the_name_construct.the_name);
    } else {
      the_text = print_name(the_type.short_name());
    }
    @Nullable string link = the_naming_strategy.link_to_type(the_type, printer_mode.STYLISH);
    assert link != null;
    @Nullable string fragment_id = the_xref_context.get_naming_strategy(the_type).
        fragment_of_construct(the_name_construct, printer_mode.STYLISH);
    if (fragment_id != null) {
      link = new base_string(link, text_library.FRAGMENT_SEPARATOR, fragment_id);
    }
    return text_util.make_html_link(the_text, link);
  }

  private text_fragment render_declaration(origin the_origin) {
    @Nullable declaration the_declaration = the_xref_context.origin_to_declaration(the_origin);

    if (!(the_declaration instanceof type_declaration)) {
      return new base_string("" + the_declaration);
    }

    type_declaration the_type_declaration = (type_declaration) the_declaration;
    principal_type the_type = the_type_declaration.get_declared_type();
    text_fragment the_text = print_name(the_type.short_name());
    @Nullable string link = the_naming_strategy.link_to_declaration(the_type_declaration,
        printer_mode.STYLISH);
    if (link != null) {
      if (the_origin instanceof type_declaration_construct) {
        @Nullable string fragment_id = the_naming_strategy.fragment_of_construct(
            (construct) the_origin, printer_mode.STYLISH);
        if (fragment_id != null) {
          link = new base_string(link, text_library.FRAGMENT_SEPARATOR, fragment_id);
        }
      }
      the_text = text_util.make_html_link(the_text, link);
    }
    return the_text;
  }

  private static base_string print_name(action_name the_name) {
    // TODO: fail gracefully if name is not a simple_name
    return printer_util.print_simple_name((simple_name) the_name, true);
  }
}

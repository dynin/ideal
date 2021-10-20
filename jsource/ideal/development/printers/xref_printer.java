/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
public class xref_printer extends base_printer {

  private static final string XREF_SEPARATOR = new base_string(" / ");

  public xref_printer(naming_strategy the_naming_strategy) {
    super(printer_mode.XREF, the_naming_strategy);
    assert the_naming_strategy != null;
  }

  private naming_strategy the_naming_strategy() {
    return (naming_strategy) the_assistant;
  }

  private xref_context the_xref_context() {
    return the_naming_strategy().the_xref_context();
  }

  @Override
  protected text_fragment print_type_body(type_declaration_construct the_construct) {
    analyzable the_analyzable = the_xref_context().get_analyzable(the_construct);

    type_declaration the_declaration = (type_declaration) the_analyzable;
    if (the_declaration.has_errors()) {
      return text_utilities.EMPTY_FRAGMENT;
    }

    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(get_links("Declaration", the_declaration, xref_mode.DECLARATION, true));
    fragments.append(get_links("Announcements", the_declaration, xref_mode.ANNOUNCEMENT, true));
    fragments.append(get_links("Direct supertypes", the_declaration, xref_mode.DIRECT_SUPERTYPE,
        true));
    fragments.append(get_links("All supertypes", the_declaration, xref_mode.INDIRECT_SUPERTYPE,
        true));
    fragments.append(get_links("Direct subtypes", the_declaration, xref_mode.DIRECT_SUPERTYPE,
        false));
    fragments.append(get_links("All subtypes", the_declaration, xref_mode.INDIRECT_SUPERTYPE,
        false));
    fragments.append(get_links("Use", the_declaration, xref_mode.USE, true));

    fragments.append(super.print_type_body(the_construct));

    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment process_variable(variable_construct c) {
    text_fragment variable_text = super.process_variable(c);

    if (the_xref_context().is_ignorable(c)) {
      return variable_text;
    }

    analyzable the_analyzable = the_xref_context().get_analyzable(c);

    declaration the_declaration = (declaration) the_analyzable;
    if (the_declaration == null) {
      // parameter in a non-implemented method
      return variable_text;
    }
    if (the_declaration.has_errors() ||
        the_declaration instanceof type_parameter_declaration ||
        ((variable_declaration) the_declaration).get_category() == variable_category.LOCAL) {
      return variable_text;
    }

    list<text_fragment> fragments = new base_list<text_fragment>(variable_text);
    fragments.append(get_links("Declaration", the_declaration, xref_mode.DECLARATION, true));
    // TODO: print override
    fragments.append(get_links("Use", the_declaration, xref_mode.USE, true));

    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment process_supertype(supertype_construct c) {
    return text_utilities.EMPTY_FRAGMENT;
  }

  @Override
  public text_fragment process_procedure(procedure_construct c) {
    text_fragment procedure_text = super.process_procedure(c);

    analyzable the_analyzable = the_xref_context().get_analyzable(c);

    declaration the_declaration = (declaration) the_analyzable;
    if (the_declaration.has_errors()) {
      return procedure_text;
    }

    list<text_fragment> fragments = new base_list<text_fragment>(procedure_text);
    fragments.append(get_links("Declaration", the_declaration, xref_mode.DECLARATION, true));
    fragments.append(get_links("Direct overrides", the_declaration, xref_mode.DIRECT_OVERRIDE,
        true));
    fragments.append(get_links("Indirectly overrides", the_declaration, xref_mode.INDIRECT_OVERRIDE,
        true));
    fragments.append(get_links("Directly overriden by", the_declaration, xref_mode.DIRECT_OVERRIDE,
        false));
    fragments.append(get_links("Indirectly overriden by", the_declaration,
        xref_mode.INDIRECT_OVERRIDE, false));
    fragments.append(get_links("Use", the_declaration, xref_mode.USE, true));

    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment print_procedure_body(@Nullable construct body) {
    return text_utilities.EMPTY_FRAGMENT;
  }

  private text_fragment get_links(String name, declaration the_declaration, xref_mode mode,
      boolean targets) {
    @Nullable readonly_list<origin> links;

    if (targets) {
      links = the_xref_context().get_targets(the_declaration, mode);
    } else {
      links = the_xref_context().get_sources(the_declaration, mode);
    }

    if (links == null || links.is_empty()) {
      return text_utilities.EMPTY_FRAGMENT;
    }

    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(styles.wrap(styles.xref_name_style, new base_string(name, ":")));

    for (int i = 0; i < links.size(); ++i) {
      fragments.append(i == 0 ? print_space() : XREF_SEPARATOR);
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
      fragments.append(render_origin(the_origin, mode));
    }

    return styles.wrap(styles.xref_links_style, text_utilities.join(fragments));
  }

  private text_fragment render_origin(origin the_origin, xref_mode mode) {
    @Nullable construct the_construct = printer_util.unwrap_name(the_origin);
    if (the_construct != null) {
      return render_name(the_construct);
    } else {
      return render_declaration(the_origin, mode);
    }
  }

  private text_fragment render_name(construct the_construct) {
    principal_type the_type = the_xref_context().get_enclosing_type(the_construct);
    text_fragment the_text;
    if (the_type == the_naming_strategy().get_current_type()) {
      the_text = print_action_name(get_name(the_construct));
    } else {
      the_text = print_action_name(the_type.short_name());
    }
    @Nullable string link = the_naming_strategy().link_to_construct(the_construct,
        printer_mode.STYLISH);
    if (link != null) {
      return text_utilities.make_html_link(the_text, link);
    } else {
      return the_text;
    }
  }

  private action_name get_name(construct the_construct) {
    if (the_construct instanceof name_construct) {
      return ((name_construct) the_construct).the_name;
    } else if (the_construct instanceof resolve_construct) {
      return ((resolve_construct) the_construct).the_name;
    } else {
      utilities.panic("get_name() failed for " + the_construct);
      return null;
    }
  }

  private text_fragment render_declaration(origin the_origin, xref_mode mode) {
    @Nullable declaration the_declaration = the_xref_context().origin_to_declaration(the_origin);
    action_name name;
    @Nullable string link = null;

    if (the_declaration instanceof type_declaration) {
      type_declaration the_type_declaration = (type_declaration) the_declaration;
      name = the_type_declaration.short_name();
      if (the_xref_context().has_output_type(the_type_declaration.get_declared_type())) {
        type_declaration_construct the_construct =
            (type_declaration_construct) printer_util.find_construct(the_origin);
        assert the_construct != null;
        link = the_naming_strategy().link_to_type_declaration(the_type_declaration,
            the_construct, printer_mode.STYLISH);
      }
    } else if (the_declaration instanceof type_announcement) {
      name = ((type_announcement) the_declaration).short_name();
    } else if (the_declaration instanceof variable_declaration) {
      name = ((variable_declaration) the_declaration).short_name();
    } else if (the_declaration instanceof procedure_declaration) {
      procedure_declaration the_procedure_declaration = (procedure_declaration) the_declaration;
      if (mode == xref_mode.DIRECT_OVERRIDE || mode == xref_mode.INDIRECT_OVERRIDE) {
        name = the_procedure_declaration.declared_in_type().short_name();
      } else {
        name = the_procedure_declaration.short_name();
      }
    } else {
      utilities.panic("Unrecognized declaration " + the_declaration);
      return null;
    }

    text_fragment the_text = print_action_name(name);
    if (link == null) {
      link = the_naming_strategy().declaration_link(the_declaration, printer_mode.STYLISH);
    }
    if (link != null) {
      return text_utilities.make_html_link(the_text, link);
    } else {
      return the_text;
    }
  }

  @Override
  public text_fragment print_special_name(special_name name) {
    if (name == special_name.IMPLICIT_CALL) {
      return print_word("constructor");
    } else {
      return super.print_special_name(name);
    }
  }
}

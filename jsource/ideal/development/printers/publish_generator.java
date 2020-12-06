/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import static ideal.runtime.texts.text_library.*;
import ideal.runtime.logs.*;

import ideal.development.elements.*;
import ideal.development.comments.*;
import ideal.development.actions.*;
import ideal.development.scanners.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.analyzers.*;
import ideal.development.parsers.*;
import ideal.development.documenters.*;
import ideal.development.declarations.*;
import ideal.development.transformers.content_writer;

public class publish_generator {

  public static final boolean GENERATE_XREF = true;

  public static final simple_name ASSETS_NAME = simple_name.make("assets");
  public static final simple_name IDEAL_STYLE_NAME = simple_name.make("ideal_style");

  private final analysis_context the_context;
  private final content_writer processor;
  public final xref_context the_xref_context;
  private final list<type_declaration_construct> extra_declarations;

  public publish_generator(analysis_context the_context, content_writer processor) {
    this.the_context = the_context;
    this.processor = processor;
    this.the_xref_context = new xref_context(the_context);
    this.extra_declarations = new base_list<type_declaration_construct>();
  }

  public void add_type(principal_type the_type) {
    type_declaration the_declaration = declaration_util.get_type_declaration(the_type);

    type_declaration_construct the_declaration_construct = (type_declaration_construct)
        declaration_util.to_type_declaration(the_declaration).deeper_origin();

    if (generate_subpages(the_declaration)) {
      list<construct> namespace_body = new base_list<construct>();
      readonly_list<declaration> signature = the_declaration.get_signature();
      for (int i = 0; i < signature.size(); ++i) {
        @Nullable type_declaration sub_declaration =
            declaration_util.to_type_declaration(signature.get(i));

        if (sub_declaration == null) {
          continue;
        }

        add_type(sub_declaration.get_declared_type());

        type_announcement_construct the_announcement = to_announcement(sub_declaration);
        namespace_body.append(the_announcement);
        the_context.put_analyzable(the_announcement, (type_declaration_analyzer) sub_declaration);
      }
      extra_declarations.append(the_declaration_construct);
      type_declaration_construct namespace_declaration =
          new type_declaration_construct(the_declaration_construct.annotations,
              the_declaration_construct.kind, the_declaration_construct.name,
              the_declaration_construct.parameters, namespace_body, the_declaration_construct);
      the_context.put_analyzable(namespace_declaration, (analyzable) the_declaration);
      the_xref_context.add_output_declaration(the_type, namespace_declaration);
    } else {
      the_xref_context.add_output_declaration(the_type, the_declaration_construct);
    }
  }

  private type_announcement_construct to_announcement(type_declaration the_type_declaration) {
    @Nullable text_fragment summary_text = null;
    @Nullable documentation the_documentation =
        the_type_declaration.annotations().the_documentation();
    if (the_documentation != null) {
      summary_text = the_documentation.section(documentation_section.SUMMARY);
    }

    readonly_list<annotation_construct> annotations;
    if (summary_text == null) {
      annotations = new empty<annotation_construct>();
    } else {
      // TODO: handle text_fragment here.
      string summary = (base_string) summary_text;
      comment the_comment = new comment(comment_type.BLOCK_DOC_COMMENT, summary, summary);
      annotations = new base_list<annotation_construct>(
          new comment_construct(the_comment, the_type_declaration));
    }

    return new type_announcement_construct(annotations, the_type_declaration.get_kind(),
            the_type_declaration.short_name(), the_type_declaration);
  }

  private boolean generate_subpages(type_declaration the_type_declaration) {
    readonly_list<declaration> signature = the_type_declaration.get_signature();
    if (signature.is_empty()) {
      return false;
    }
    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof type_announcement ||
          the_declaration instanceof type_declaration ||
          the_declaration instanceof import_declaration) {
        continue;
      } else {
        return false;
      }
    }

    return true;
  }

  private void generate_navigation_xref() {
    immutable_list<type_declaration> all_declarations = the_xref_context.output_declarations();
    for (int i = 0; i < all_declarations.size(); ++i) {
      type_declaration the_type_declaration = all_declarations.get(i);
      readonly_list<declaration> signature = the_type_declaration.get_signature();
      @Nullable type_declaration previous = null;
      for (int j = 0; j < signature.size(); ++j) {
        @Nullable type_declaration sub_declaration =
            declaration_util.to_type_declaration(signature.get(j));
        if (sub_declaration == null) {
          continue;
        }
        if (previous != null) {
          the_xref_context.add_successor(previous, sub_declaration);
        }
        previous = sub_declaration;
      }
    }
  }

  private void populate_declaration(type_declaration_construct the_declaration_construct) {
    type_declaration the_type_declaration = declaration_util.to_type_declaration(
        the_context.get_analyzable(the_declaration_construct));
    new populate_xref(the_xref_context, the_type_declaration.get_declared_type()).
        process(the_declaration_construct);
  }

  public void generate_all() {
    immutable_list<type_declaration_construct> constructs = the_xref_context.output_constructs();
    for (int i = 0; i < constructs.size(); ++i) {
      populate_declaration(constructs.get(i));
    }
    // TODO: just populate announcements here.
    for (int i = 0; i < extra_declarations.size(); ++i) {
      populate_declaration(extra_declarations.get(i));
    }

    generate_navigation_xref();

    for (int i = 0; i < constructs.size(); ++i) {
      type_declaration_construct the_declaration_construct = constructs.get(i);
      type_declaration the_declaration = declaration_util.to_type_declaration(
          the_context.get_analyzable(the_declaration_construct));
      generate_markup(new base_list<construct>(the_declaration_construct),
          the_xref_context.get_naming_strategy(the_declaration.get_declared_type()));
    }
  }

  public void generate_markup(readonly_list<construct> constructs,
      naming_strategy the_naming_strategy) {

    base_printer printer = new base_printer(printer_mode.STYLISH, the_naming_strategy);
    text_fragment body = printer.print_statements(constructs);
    text_fragment result = render_page(body, the_naming_strategy, printer_mode.STYLISH);

    string result_string = text_util.to_markup_string(result);
    processor.write(result_string, the_naming_strategy.get_full_names(), base_extension.HTML);

    if (!GENERATE_XREF) {
      return;
    }

    xref_printer the_xref_printer = new xref_printer(the_naming_strategy);
    text_fragment xref_body = the_xref_printer.print_statements(constructs);
    text_fragment xref_result = render_page(xref_body, the_naming_strategy, printer_mode.XREF);

    string xref_string = text_util.to_markup_string(xref_result);
    processor.write((base_string) xref_string, the_naming_strategy.get_xref_names(),
        base_extension.HTML);
  }

  text_fragment render_page(text_fragment body, naming_strategy the_naming_strategy,
      printer_mode mode) {
    body = styles.wrap(styles.main_style, new html_rewriter().rewrite(body));

    text_element navigation = make_navigation(the_naming_strategy, mode);
    body = text_util.join(navigation, body, navigation);
    return wrap_body(body, the_naming_strategy.get_full_names(), the_naming_strategy);
  }

  private static text_fragment make_title(readonly_list<simple_name> full_name) {
    list<text_fragment> result = new base_list<text_fragment>();

    // TODO: replace with join()
    for (int i = 0; i < full_name.size(); ++i) {
      if (i > 0) {
        result.append(base_printer.bullet_fragment);
      }
      // TODO: use code from the base_printer that generates spaces.
      result.append((base_string) full_name.get(i).to_string());
    }

    if (false) {
      result.append(new base_string(" "));
      result.append(MDASH);
      result.append(new base_string(" testing HTML output"));
    }

    return text_util.join(result);
  }

  private text_element make_navigation(naming_strategy the_naming_strategy, printer_mode mode) {
    principal_type the_type = the_naming_strategy.get_current_type();
    @Nullable type_declaration the_declaration = declaration_util.get_type_declaration(the_type);

    text_element left = make_nav_cell(
        the_xref_context.get_predecessor(the_declaration), true,
        the_naming_strategy, mode);
    text_element center = make_center_cell(the_type.get_parent(), the_naming_strategy, mode);
    text_element right = make_nav_cell(
        the_xref_context.get_successor(the_declaration), false,
        the_naming_strategy, mode);
    text_element row = text_util.make_element(TR, new base_list<text_node>(left, center, right));
    return base_element.make(TABLE, text_library.CLASS, styles.nav_table_style, row);
  }

  private static base_string print_name(action_name the_name) {
    // TODO: fail gracefully if name is not a simple_name
    return printer_util.print_simple_name((simple_name) the_name, true);
  }

  private text_element make_nav_cell(@Nullable origin the_origin, boolean left,
      naming_strategy the_naming_strategy, printer_mode mode) {
    @Nullable principal_type the_type = the_origin instanceof type_declaration ?
        ((type_declaration) the_origin).get_declared_type() : null;
    text_fragment the_text;

    if (the_type != null) {
      the_text = print_name(the_type.short_name());
      @Nullable string link = the_naming_strategy.link_to_type(the_type, mode);
      if (link != null) {
        the_text = text_util.make_html_link(the_text, link);
      }
      if (left) {
        the_text = text_util.join(text_library.LARR, text_library.NBSP, the_text);
      } else {
        the_text = text_util.join(the_text, text_library.NBSP, text_library.RARR);
      }
    } else {
      the_text = null;
    }

    return base_element.make(TD, text_library.CLASS, left ? styles.nav_left_style :
        styles.nav_right_style, the_text);
  }

  private text_element make_center_cell(@Nullable principal_type the_type,
      naming_strategy the_naming_strategy, printer_mode mode) {
    text_fragment the_text = null;
    principal_type current_type = the_type;

    while (current_type != null) {
      action_name current_name = current_type.short_name();
      if (current_name instanceof simple_name) {
        text_fragment name_text = print_name(current_name);
        @Nullable string link = the_naming_strategy.link_to_type(current_type, mode);
        if (link != null) {
          name_text = text_util.make_html_link(name_text, link);
        }
        if (the_text != null) {
          the_text = text_util.join(name_text, base_printer.bullet_fragment, the_text);
        } else {
          the_text = name_text;
        }
      }
      current_type = current_type.get_parent();
    }

    return base_element.make(TD, text_library.CLASS, styles.nav_center_style, the_text);
  }

  private static text_fragment wrap_body(text_fragment body_text,
      readonly_list<simple_name> full_name, naming_strategy the_naming_strategy) {
    text_element title = text_util.make_element(TITLE, text_util.to_list(make_title(full_name)));
    // TODO: introduce constants.
    base_string css_href = the_naming_strategy.link_to_resource(
        new base_list<simple_name>(ASSETS_NAME, IDEAL_STYLE_NAME), base_extension.CSS);
    list_dictionary<attribute_id, string> attributes = new list_dictionary<attribute_id, string>();
    attributes.put(HREF, css_href);
    attributes.put(REL, new base_string("stylesheet"));
    attributes.put(TYPE, new base_string("text/css"));
    text_element link = new base_element(LINK, attributes, null);
    text_element head = text_util.make_element(HEAD, new base_list<text_node>(title, link));
    text_element body = base_element.make(BODY, body_text);
    return text_util.make_element(HTML, new base_list<text_node>(head, body));
  }
}

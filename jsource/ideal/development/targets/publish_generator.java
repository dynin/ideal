/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.targets;

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
import ideal.development.printers.*;
import ideal.development.documenters.*;
import ideal.development.declarations.*;
import ideal.development.transformers.content_writer;

public class publish_generator {

  public static final simple_name ASSETS_NAME = simple_name.make("assets");
  public static final simple_name IDEAL_STYLE_NAME = simple_name.make("ideal_style");

  private final analysis_context the_context;
  private final content_writer processor;
  private final xref_context the_xref_context;
  private final populate_xref the_populate_xref;
  private final list<type_declaration_construct> output_declarations;

  public publish_generator(analysis_context the_context, content_writer processor) {
    this.the_context = the_context;
    this.processor = processor;
    this.the_xref_context = new xref_context();
    this.the_populate_xref = new populate_xref(the_context, the_xref_context);
    this.output_declarations = new base_list<type_declaration_construct>();
  }

  public void add_type(principal_type the_type) {
    assert the_type.get_declaration() instanceof type_declaration;
    type_declaration the_declaration = (type_declaration) the_type.get_declaration();

    type_declaration_construct the_declaration_construct =
        (type_declaration_construct) (get_type_declaration(the_declaration).deeper_origin());
    the_populate_xref.process(the_declaration_construct);

    if (the_type.get_kind().is_namespace()) {
      list<construct> namespace_body = new base_list<construct>();
      // TODO: we should handle other subdeclarations here,
      //  such as variable declarations.
      readonly_list<type_declaration> sub_declarations =
          target_utilities.get_declared_types(the_declaration);
      for (int i = 0; i < sub_declarations.size(); ++i) {
        type_declaration sub_declaration = get_type_declaration(sub_declarations.get(i));
        if (i < sub_declarations.size() - 1) {
          the_xref_context.add(sub_declaration, xref_mode.SUCCESSOR, sub_declarations.get(i + 1));
        }
        add_type(sub_declaration.get_declared_type());

        @Nullable text_fragment summary_text = null;
        @Nullable documentation the_documentation =
            sub_declaration.annotations().the_documentation();
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
              new comment_construct(the_comment, sub_declaration));
        }

        type_announcement_construct the_announcement =
            new type_announcement_construct(annotations, sub_declaration.get_kind(),
                sub_declaration.short_name(), sub_declaration);
        namespace_body.append(the_announcement);
        the_context.put_analyzable(the_announcement, (type_declaration_analyzer) sub_declaration);
      }
      type_declaration_construct namespace_declaration =
          new type_declaration_construct(the_declaration_construct.annotations,
              the_declaration_construct.kind, the_declaration_construct.name,
              the_declaration_construct.parameters, namespace_body, the_declaration_construct);
      the_context.put_analyzable(namespace_declaration, (analyzable) the_declaration);
      add_output_declaration(the_type, namespace_declaration);
    } else {
      add_output_declaration(the_type, the_declaration_construct);
    }
  }

  private void add_output_declaration(principal_type the_type,
      type_declaration_construct the_declaration_construct) {
    type_declaration the_type_declaration =
        get_type_declaration(the_context.get_analyzable(the_declaration_construct));
    assert the_type_declaration.get_declared_type() == the_type;
    output_declarations.append(the_declaration_construct);
  }

  public void generate_all() {
    for (int i = 0; i < output_declarations.size(); ++i) {
      type_declaration_construct the_declaration_construct = output_declarations.get(i);
      type_declaration the_declaration =
          get_type_declaration(the_context.get_analyzable(the_declaration_construct));
      generate_markup(new base_list<construct>(the_declaration_construct),
          new naming_strategy(the_declaration.get_declared_type(), the_context));
    }
  }

  private type_declaration get_type_declaration(origin the_origin) {
    if (the_origin instanceof type_announcement) {
      return ((type_announcement) the_origin).get_type_declaration();
    } else if (the_origin instanceof type_declaration) {
      return (type_declaration) the_origin;
    } else {
      utilities.panic("Type declaration expected");
      return null;
    }
  }

  public void generate_markup(readonly_list<construct> constructs,
      naming_strategy the_naming_strategy) {

    base_printer printer = the_naming_strategy.get_printer();
    text_fragment body = printer.print_statements(constructs);
    text_fragment result = render_page(body, the_naming_strategy);

    string result_string = text_util.to_markup_string(result);
    processor.write(result_string, the_naming_strategy.get_full_names(), base_extension.HTML);

    xref_printer the_xref_printer = new xref_printer(the_context, the_xref_context,
        the_naming_strategy);
    text_fragment xref_body = the_xref_printer.print_statements(constructs);
    text_fragment xref_result = render_page(xref_body, the_naming_strategy);

    string xref_string = text_util.to_markup_string(xref_result);
    processor.write((base_string) xref_string, the_naming_strategy.get_xref_names(),
        base_extension.HTML);
  }

  text_fragment render_page(text_fragment body, naming_strategy the_naming_strategy) {
    body = styles.wrap(styles.main_style, new html_rewriter().rewrite(body));

    text_element navigation = make_navigation(the_naming_strategy);
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

  private text_element make_navigation(naming_strategy the_naming_strategy) {
    principal_type the_type = the_naming_strategy.get_current_type();
    @Nullable declaration the_declaration = the_naming_strategy.get_current_declaration();

    text_element left = make_nav_cell(
        the_xref_context.get_source(the_declaration, xref_mode.SUCCESSOR), true,
        the_naming_strategy);
    text_element center = make_center_cell(the_type.get_parent(), the_naming_strategy);
    text_element right = make_nav_cell(
        the_xref_context.get_target(the_declaration, xref_mode.SUCCESSOR), false,
        the_naming_strategy);
    text_element row = text_util.make_element(TR, new base_list<text_node>(left, center, right));
    return base_element.make(TABLE, text_library.CLASS, styles.nav_table_style, row);
  }

  private text_element make_nav_cell(@Nullable origin the_origin, boolean left,
      naming_strategy the_naming_strategy) {
    @Nullable principal_type the_type = the_origin instanceof type_declaration ?
        ((type_declaration) the_origin).get_declared_type() : null;
    text_fragment the_text;

    if (the_type != null) {
      the_text = the_naming_strategy.print_simple_name((simple_name) the_type.short_name());
      @Nullable string link = the_naming_strategy.link_to_type(the_type, link_mode.STYLISH);
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
      naming_strategy the_naming_strategy) {
    text_fragment the_text = null;
    principal_type current_type = the_type;

    while (current_type != null) {
      action_name current_name = current_type.short_name();
      if (current_name instanceof simple_name) {
        text_fragment name_text = the_naming_strategy.print_simple_name((simple_name) current_name);
        @Nullable string link = the_naming_strategy.link_to_type(current_type, link_mode.STYLISH);
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
    base_string css_href = the_naming_strategy.link_to(
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

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
 * Convert constructs into formatted text.
 */
public class base_printer extends construct_visitor<text_fragment> implements printer {

  protected final printer_mode the_mode;
  protected final @Nullable printer_assistant the_assistant;

  private static final string_text_node SPACE = new base_string(" ");
  // TODO: define token
  private static final string PASS = new base_string("pass");
  private static final string RETURN = new base_string("return");

  public static final text_fragment bullet_fragment =
      base_list_text_node.make(SPACE, text_library.BULL, SPACE);

  public base_printer(printer_mode the_mode, @Nullable printer_assistant the_assistant) {
    this.the_mode = the_mode;
    this.the_assistant = the_assistant;
  }

  public base_printer(printer_mode the_mode) {
    this(the_mode, null);
  }

  public boolean is_curly_mode() {
    return the_mode == printer_mode.CURLY;
  }

  public boolean is_stylish_mode() {
    return the_mode == printer_mode.STYLISH || the_mode == printer_mode.XREF;
  }

  public text_fragment print(construct c) {
    assert c != null;
    text_fragment result = process(c);
    assert result != null;

    return result;
  }

  public text_fragment print_space() {
    return SPACE;
  }

  public text_fragment print_line(text_fragment fragment) {
    return new base_element(text_library.DIV, fragment);
  }

  public text_fragment print_word(string s) {
    return (string_text_node) s;
  }

  // TODO: remove references to java.lang.String so this can be retired.
  public text_fragment print_word(String s) {
    return new base_string(s);
  }

  public text_fragment print_word(token_type word) {
    return print_word(word.name());
  }

  public text_fragment print_punctuation(token_type punct) {
    return print_word(punct);
  }

  protected text_fragment print_block(readonly_list<construct> constructs, boolean prepend_space,
      boolean end_line) {
    assert constructs != null;
    @Nullable text_fragment statements = constructs.is_empty() ?  null :
        print_statements(constructs);
    return wrap_block(statements, prepend_space, end_line);
  }

  protected text_fragment wrap_block(@Nullable text_fragment statements, boolean prepend_space,
      boolean end_line) {

    if (is_curly_mode()) {
      list<text_fragment> fragments = new base_list<text_fragment>();
      if (prepend_space) {
        fragments.append(print_space());
      }
      fragments.append(print_punctuation(punctuation.OPEN_BRACE));
      text_fragment close_brace = print_punctuation(punctuation.CLOSE_BRACE);
      if (statements == null) {
        fragments.append(print_space());
      } else {
        fragments.append(new base_element(text_library.INDENT, statements));
        if (end_line) {
          close_brace = print_line(close_brace);
        }
      }
      fragments.append(close_brace);
      return text_utilities.join(fragments);
    } else {
      if (statements == null) {
        return new base_element(text_library.INDENT, print_pass());
      } else {
        return new base_element(text_library.INDENT, statements);
      }
    }
  }

  public text_fragment print_procedure_body(@Nullable construct body) {
    if (body == null) {
      if (is_stylish_mode()) {
        return text_utilities.EMPTY_FRAGMENT;
      } else {
        return print_empty();
      }
    }

    if (body instanceof block_construct) {
      return print_block(((block_construct) body).body, true, false);
    } else {
      list<text_fragment> fragments = new base_list<text_fragment>();
      fragments.append(print_space());
      fragments.append(print_punctuation(punctuation.EQUALS_GREATER_THAN));
      fragments.append(print_space());
      fragments.append(print(body));
      if (is_curly_mode()) {
        fragments.append(print_punctuation(punctuation.SEMICOLON));
      }
      return text_utilities.join(fragments);
    }
  }

  private boolean is_terminated(construct c) {
    if (c instanceof extension_construct) {
      return ((extension_construct) c).is_terminated();
    }

    // TODO: how do we handle comment_construct?
    return c instanceof type_declaration_construct ||
           c instanceof comment_construct ||
           c instanceof procedure_construct ||
           c instanceof block_construct ||
           c instanceof loop_construct ||
           (c instanceof conditional_construct &&
             ((conditional_construct) c).is_statement);
  }

  public text_fragment print_statements(readonly_list<? extends construct> statements) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    for (int i = 0; i < statements.size(); ++i) {
      construct the_statement = statements.get(i);
      if (is_terminated(the_statement)) {
        fragments.append(print(the_statement));
      } else {
        if (the_statement instanceof name_construct) {
          // TODO: this is a hack to use var_declaration_name_style...
          fragments.append(print_enum_value(the_statement, true));
        } else {
          fragments.append(print_statement(the_statement));
        }
      }
    }
    return text_utilities.join(fragments);
  }

  protected text_fragment print_enum_values(readonly_list<construct> constructs) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    for (int i = 0; i < constructs.size(); ++i) {
      fragments.append(print_enum_value(constructs.get(i), i == (constructs.size() - 1)));
    }
    return text_utilities.join(fragments);
  }

  protected text_fragment print_enum_value(construct the_construct, boolean last_value) {
    assert enum_util.can_be_enum_value(the_construct);
    list<text_fragment> fragments = new base_list<text_fragment>();
    if (the_construct instanceof name_construct) {
      fragments.append(styles.wrap(styles.var_declaration_name_style,
          print_action_name(((name_construct) the_construct).the_name)));
    } else {
      fragments.append(print(the_construct));
    }
    if (is_curly_mode()) {
      fragments.append(print_punctuation(enum_separator_token(last_value)));
    }
    return print_line(text_utilities.join(fragments));
  }

  public text_fragment process_error(construct c) {
    return print_line(print_word(
        name_utilities.in_brackets(new base_string("print_error: " + utilities.describe(c)))));
  }

  @Override
  public text_fragment process_default(construct c) {
    return process_error(c);
  }

  public text_fragment process_error_signal(error_signal e) {
    return print_word(name_utilities.in_brackets(e.cause.message()));
  }

  public text_fragment print_params(readonly_list<? extends construct> params,
      grouping_type grouping) {
    assert params != null;

    text_fragment inside_text;

    if (params.is_empty()) {
      inside_text = text_utilities.EMPTY_FRAGMENT;
    } else {
      boolean wrap_in_space = wrap_in_space(grouping);
      list<text_fragment> fragments = new base_list<text_fragment>();

      if (wrap_in_space) {
        fragments.append(print_space());
      }

      for (int i = 0; i < params.size(); ++i) {
        construct c = params.get(i);
        if (c instanceof variable_construct) {
          fragments.append(process_variable((variable_construct) c));
        } else {
          fragments.append(print(c));
        }

        if (i < params.size() - 1) {
          fragments.append(print_punctuation(punctuation.COMMA));
          fragments.append(print_space());
        }
      }

      if (wrap_in_space) {
        fragments.append(print_space());
      }

      inside_text = text_utilities.join(fragments);
    }

    return print_group(inside_text, grouping);
  }

  boolean wrap_in_space(grouping_type grouping) {
    return false;
  }

  @Override
  public text_fragment process_variable(variable_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_documentation(c.annotations, c));
    fragments.append(print_modifiers(c.annotations, true));

    if (c.variable_type != null && print_variable_types()) {
      fragments.append(print(c.variable_type));
      fragments.append(print_space());
    }

    text_fragment var_name = print_action_name(c.name);
    var_name = wrap_with_span_id(var_name, c);
    fragments.append(styles.wrap(styles.var_declaration_name_style,
        make_declaration_link(var_name, c)));

    fragments.append(print_modifiers(c.post_annotations, false));

    if (c.init != null) {
      fragments.append(print_space());
      fragments.append(print_word(init_token(c)));
      fragments.append(print_space());
      fragments.append(print(c.init));
    }

    return text_utilities.join(fragments);
  }

  protected boolean print_variable_types() {
    return true;
  }

  public text_fragment print_modifiers(readonly_list<annotation_construct> annotations,
      boolean prefix) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    for (int i = 0; i < annotations.size(); ++i) {
      annotation_construct the_annotation = annotations.get(i);
      if (the_annotation instanceof modifier_construct) {
        modifier_construct the_modifier = (modifier_construct) the_annotation;
        if (!is_modifier_supported(the_modifier.the_kind)) {
          return process_error(the_modifier);
        }
        if (!prefix) {
          fragments.append(print_space());
        }
        fragments.append(print(the_modifier));
        if (prefix) {
          fragments.append(print_space());
        }
      }
    }

    return text_utilities.join(fragments);
  }

  protected boolean is_modifier_supported(modifier_kind the_kind) {
    return true;
  }

  public text_fragment print_documentation(readonly_list<annotation_construct> annotations,
      construct the_declaration) {
    if (is_curly_mode()) {
      // TODO: handle doc comments in curly mode.
      return text_utilities.EMPTY_FRAGMENT;
    }

    list<text_fragment> fragments = new base_list<text_fragment>();

    for (int i = 0; i < annotations.size(); ++i) {
      annotation_construct the_annotation = annotations.get(i);
      if (the_annotation instanceof comment_construct) {
        fragments.append(styles.wrap(styles.documentation_style,
            ((comment_construct) the_annotation).section(documentation_section.ALL)));
      }
    }

    if (fragments.is_empty() && the_assistant != null) {
      @Nullable documentation parent_documentation =
          the_assistant.get_documentation(the_declaration);
      if (parent_documentation != null) {
        // TODO: share code with above.
        fragments.append(styles.wrap(styles.documentation_style,
            parent_documentation.section(documentation_section.ALL)));
      }
    }

    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment process_block(block_construct c) {
    text_fragment block = print_block(c.body, false, true);
    if (c.annotations.is_not_empty()) {
      block = text_utilities.join(print_modifiers(c.annotations, true), block);
    }
    if (is_curly_mode()) {
      block = print_line(block);
    }
    return block;
  }

  @Override
  public text_fragment print_indented_statement(construct c) {
    return do_print_indented_statement(c, true);
  }

  public text_fragment do_print_indented_statement(construct c, boolean end_line) {
    if (c instanceof block_construct) {
      block_construct bc = (block_construct) c;
      return print_block(bc.body, true, end_line);
    } else {
      return new base_element(text_library.INDENT, print_statement(c));
    }
  }

  @Override
  public text_fragment process_conditional(conditional_construct c) {
    if (c.is_statement) {
      return process_if(c);
    } else {
      return process_question(c);
    }
  }

  @Override
  public text_fragment process_constraint(constraint_construct c) {
    return text_utilities.join(print_simple_name(c.the_constraint_category.constraint_name()),
        print_space(), print(c.expr));
  }

  public text_fragment print_empty() {
    if (is_stylish_mode()) {
      return print_pass();
    } else {
      return print_punctuation(punctuation.SEMICOLON);
    }
  }

  public text_fragment print_pass() {
    assert is_stylish_mode();
    // TODO: this should be a token
    return print_word(PASS);
  }

  @Override
  public text_fragment process_empty(empty_construct c) {
    return print_empty();
  }

  @Override
  public text_fragment process_comment(comment_construct c) {
    // TODO: shouldn't print_line here, depending on comment type.
    return print_line((base_string) c.the_comment.image);
  }

  @Override
  public text_fragment process_extension(extension_construct c) {
    return c.print(this);
  }

  @Override
  public text_fragment process_procedure(procedure_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_documentation(c.annotations, c));
    fragments.append(print_modifiers(c.annotations, true));
    if (c.ret != null) {
      fragments.append(print(c.ret));
      fragments.append(print_space());
    }

    text_fragment proc_name = print_action_name(c.name);
    proc_name = wrap_with_span_id(proc_name, c);
    fragments.append(styles.wrap(styles.procedure_declaration_name_style,
        make_declaration_link(proc_name, c)));

    if (c.parameters != null) {
      fragments.append(print_params(c.parameters, grouping_type.PARENS));
    }

    fragments.append(print_modifiers(c.post_annotations, false));
    fragments.append(print_procedure_body(c.body));

    if (is_stylish_mode()) {
      return styles.wrap(styles.procedure_declaration_style,
          text_utilities.join(fragments));
    } else {
      return print_line(text_utilities.join(fragments));
    }
  }

  @Override
  public text_fragment process_list(list_construct c) {
    // TODO: handle has_trailing_comma
    return print_params(c.the_elements, c.grouping);
  }

  @Override
  public text_fragment print_grouping_in_statement(text_fragment text) {
    if (is_stylish_mode()) {
      return text;
    } else {
      return print_group(text, grouping_type.PARENS);
    }
  }

  public text_fragment print_group(text_fragment text, grouping_type grouping) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(print_punctuation(grouping.start));
    fragments.append(text);
    fragments.append(print_punctuation(grouping.end));
    return text_utilities.join(fragments);
  }

  public text_fragment print_statement(construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(print(c));
    if (is_curly_mode()) {
      fragments.append(print_punctuation(punctuation.SEMICOLON));
    }
    return print_line(text_utilities.join(fragments));
  }

  public text_fragment print_simple_name(simple_name name) {
    return print_word(printer_util.print_simple_name(name, is_stylish_mode()));
  }

  public text_fragment print_action_name(action_name id) {
    if (id instanceof simple_name) {
      return print_simple_name((simple_name) id);
    } else if (id instanceof operator) {
      return print_word(((operator) id).name);
    } else if (id instanceof special_name) {
      return print_special_name((special_name) id);
    } else {
      utilities.panic("unknown identifier: " + id);
      return null;
    }
  }

  public text_fragment print_special_name(special_name name) {
    // TODO: simplify, convert to set of special_names
    if (name == special_name.THIS) {
      return print_word(keywords.THIS);
    } else if (name == special_name.SUPER) {
      return print_word(keywords.SUPER);
    } else if (name == special_name.NEW) {
      // TODO: handle new as operator?
      return print_word(keywords.NEW);
    } else {
      return print_word("ERROR:" + name);
      //utilities.panic("unknown special name: " + name);
      //return null;
    }
  }

  protected text_fragment make_link(text_fragment the_text, construct the_construct) {
    if (the_assistant != null) {
      @Nullable string link = the_assistant.link_to_declaration(the_construct,
          printer_mode.STYLISH);
      if (link != null) {
        return text_utilities.make_html_link(the_text, link);
      }
    }
    return the_text;
  }

  protected text_fragment make_declaration_link(text_fragment the_text, construct the_construct) {
    if (the_assistant != null) {
      printer_mode link_mode = (the_mode == printer_mode.XREF) ? printer_mode.STYLISH :
          printer_mode.XREF;
      @Nullable string link = the_assistant.link_to_construct(the_construct, link_mode);
      if (link == null && naming_strategy.DEBUG_FRAGMENTS) {
        System.out.println("NOLINK " + the_construct + " TEXT " + the_text);
      }
      if (link != null) {
        return text_utilities.make_html_link(the_text, link);
      }
    }
    return the_text;
  }

  protected text_fragment make_name(action_name the_name, construct c) {
    text_fragment name = print_action_name(the_name);
    if (the_name instanceof simple_name) {
      name = wrap_with_span_id(name, c);
      return make_link(name, c);
    } else {
      return name;
    }
  }

  @Override
  public text_fragment process_name(name_construct c) {
    return make_name(c.the_name, c);
  }

  public text_fragment process_question(conditional_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(print(c.cond_expr));
    fragments.append(print_space());
    fragments.append(print_punctuation(punctuation.QUESTION_MARK));
    fragments.append(print_space());
    fragments.append(print(c.then_expr));
    fragments.append(print_space());
    fragments.append(print_punctuation(punctuation.COLON));
    fragments.append(print_space());
    fragments.append(print(c.else_expr));
    return text_utilities.join(fragments);
  }

  public text_fragment process_if(conditional_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_word(keywords.IF));
    fragments.append(print_space());
    fragments.append(print_grouping_in_statement(print(c.cond_expr)));

    if (c.else_expr != null) {
      fragments.append(do_print_indented_statement(c.then_expr, false));
      fragments.append(print_space());
      fragments.append(print_word(keywords.ELSE));
      if (is_chained_else(c.else_expr)) {
        fragments.append(print_space());
        fragments.append(print(c.else_expr));
      } else {
        fragments.append(print_indented_statement(c.else_expr));
      }
    } else {
      fragments.append(print_indented_statement(c.then_expr));
    }

    return text_utilities.join(fragments);
  }

  public boolean is_chained_else(construct else_construct) {
    return else_construct instanceof conditional_construct &&
        ((conditional_construct) else_construct).is_statement;
  }

  @Override
  public text_fragment process_import(import_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_documentation(c.annotations, c));
    fragments.append(print_modifiers(c.annotations, true));
    fragments.append(print_word(keywords.IMPORT));
    fragments.append(print_space());
    fragments.append(print(c.type_construct));

    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment process_flavor(flavor_construct c) {
    return text_utilities.join(print_flavor(c.flavor), print_space(),  print(c.expr));
  }

  @Override
  public text_fragment process_modifier(modifier_construct c) {
    if (c.the_kind == general_modifier.varargs_modifier) {
      return print_punctuation(punctuation.ELLIPSIS);
    }

    text_fragment result = print_modifier_kind(c.the_kind);

    /*
    if (c.parameters != null) {
      result = text_utilities.join(result, process_list(c.parameters));
    }
    */

    return result;
  }

  protected text_fragment print_modifier_kind(modifier_kind the_kind) {
    return print_simple_name(the_kind.name());
  }

  public text_fragment print_flavor(type_flavor flavor) {
    return print_simple_name(flavor.name());
  }

  public text_fragment print_infix(operator_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(print(c.arguments.get(0)));
    fragments.append(print_space());
    fragments.append(print_operator_name(c.the_operator));
    fragments.append(print_space());
    fragments.append(print(c.arguments.get(1)));
    return text_utilities.join(fragments);
  }

  public text_fragment print_operator_name(operator the_operator) {
    return print_word(the_operator.name);
  }

  public text_fragment print_prefix(operator_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(print_word(c.the_operator.name));
    if (!(c.the_operator.name instanceof punctuation_type)) {
      fragments.append(print_space());
    }
    fragments.append(print(c.arguments.first()));
    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment process_parameter(parameter_construct c) {
    return text_utilities.join(print(c.main), print_params(c.parameters, c.grouping));
  }

  @Override
  public text_fragment process_operator(operator_construct c) {
    operator op = c.the_operator;
    if (op.the_operator_type == operator_type.PREFIX) {
      assert c.arguments.size() == 1;
      return print_prefix(c);
    } else if (op.the_operator_type == operator_type.INFIX) {
      assert c.arguments.size() == 2;
      return print_infix(c);
    } else {
      utilities.panic("unsupported op " + op);
      return null;
    }
  }

  @Override
  public text_fragment process_resolve(resolve_construct c) {
    return text_utilities.join(print(c.qualifier), print_connector_dot(), make_name(c.the_name, c));
  }

  public text_fragment print_connector_dot() {
    if (is_stylish_mode()) {
      return bullet_fragment;
    } else {
      return print_punctuation(punctuation.DOT);
    }
  }

  @Override
  public text_fragment process_return(return_construct c) {
    text_fragment return_keyword = print_word(RETURN);

    if (c.the_expression != null && !(c.the_expression instanceof empty_construct)) {
      return text_utilities.join(return_keyword, print_space(), print(c.the_expression));
    } else {
      return return_keyword;
    }
  }

  @Override
  public text_fragment process_supertype(supertype_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_modifiers(c.annotations, true));

    if (c.subtype_flavor != null) {
      fragments.append(print_flavor(c.subtype_flavor));
      fragments.append(print_space());
    }
    fragments.append(print_simple_name(c.tag.name()));
    fragments.append(print_space());

    readonly_list<construct> types = c.type_constructs;
    for (int i = 0; i < types.size(); ++i) {
      fragments.append(styles.wrap(styles.supertype_declaration_name_style, print(types.get(i))));
      if (i < types.size() - 1) {
        fragments.append(print_punctuation(punctuation.COMMA));
        fragments.append(print_space());
      }
    }

    return text_utilities.join(fragments);
  }

  protected text_fragment print_type_parameters(readonly_list<construct> parameters) {
    return print_params(parameters, grouping_type.BRACKETS);
  }

  protected text_fragment print_type_start(type_declaration_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_modifiers(c.annotations, true));

    fragments.append(print_simple_name(c.kind.name()));
    fragments.append(print_space());

    text_fragment type_name = print_action_name(c.name);
    type_name = wrap_with_span_id(type_name, c);
    // TODO: make the style a parameter
    fragments.append(styles.wrap(styles.type_declaration_name_style,
        make_declaration_link(type_name, c)));

    if (c.has_parameters()) {
      fragments.append(print_type_parameters(c.parameters));
    }

    return text_utilities.join(fragments);
  }

  private text_fragment wrap_with_span_id(text_fragment text, construct c) {
    if (the_assistant != null) {
      @Nullable string id = the_assistant.fragment_of_construct(c, printer_mode.STYLISH);
      if (id != null) {
        return new base_element(text_library.SPAN, text_library.ID, id, text);
      }
    }
    return text;
  }

  protected text_fragment print_type_body(type_declaration_construct the_declaration) {
    readonly_list<construct> constructs = the_declaration.body;
    @Nullable text_fragment body_statements;
    if (constructs.is_empty()) {
      body_statements = null;
    } else {
      body_statements = print_statements(constructs);
    }
    return wrap_type_body(body_statements);
  }

  protected text_fragment wrap_type_body(@Nullable text_fragment body_statements) {
    return wrap_block(body_statements, true, true);
  }

  @Override
  public text_fragment process_type_declaration(type_declaration_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_documentation(c.annotations, c));

    fragments.append(styles.wrap(styles.type_declaration_style,
        text_utilities.join(print_type_start(c), print_type_body(c))));

    return print_line(text_utilities.join(fragments));
  }

  @Override
  public text_fragment process_type_announcement(type_announcement_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_documentation(c.annotations, c));

    // TODO: wrap the entire type announcement in a style?

    fragments.append(print_modifiers(c.annotations, true));

    fragments.append(print_simple_name(c.kind.name()));
    fragments.append(print_space());

    text_fragment type_name = print_action_name(c.name);
    type_name = wrap_with_span_id(type_name, c);
    fragments.append(styles.wrap(styles.type_announcement_name_style, make_link(type_name, c)));

    return text_utilities.join(fragments);
  }

  public text_fragment print_integer_literal(integer_literal the_literal) {
    return print_word(the_literal.the_value().toString());
  }

  public text_fragment print_quoted_literal(quoted_literal the_literal) {
    return text_utilities.join(print_punctuation(the_literal.quote),
        print_word(the_literal.with_escapes), print_punctuation(the_literal.quote));
  }

  @Override
  public text_fragment process_literal(literal_construct c) {
    literal the_literal = c.the_literal;

    if (the_literal instanceof integer_literal) {
      return print_integer_literal((integer_literal) the_literal);
    } else if (the_literal instanceof quoted_literal) {
      return print_quoted_literal((quoted_literal) the_literal);
    } else {
      utilities.panic("unsupported literal " + the_literal);
      return null;
    }
  }

  @Override
  public text_fragment process_loop(loop_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(print_word(keywords.LOOP));
    fragments.append(print_indented_statement(c.body));

    return text_utilities.join(fragments);
  }

  @Override
  public text_fragment process_jump(jump_construct the_construct) {
    return print_simple_name(the_construct.the_jump_category.jump_name());
  }

  public token_type init_token(variable_construct c) {
    return punctuation.COLON;
  }

  public token_type enum_separator_token(boolean last_value) {
    return punctuation.SEMICOLON;
  }
}

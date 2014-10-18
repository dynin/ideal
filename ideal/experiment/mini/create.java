/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import static ideal.experiment.mini.bootstrapped.*;
import static ideal.experiment.mini.library.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class create {

  public static abstract class construct_dispatch<result> implements function<result, construct> {

    @Override
    public result call(construct the_construct) {
      if (the_construct instanceof identifier) {
        return call_identifier((identifier) the_construct);
      }

      if (the_construct instanceof operator) {
        return call_operator((operator) the_construct);
      }

      if (the_construct instanceof string_literal) {
        return call_string_literal((string_literal) the_construct);
      }

      if (the_construct instanceof parameter_construct) {
        return call_parameter_construct((parameter_construct) the_construct);
      }

      if (the_construct instanceof modifier_construct) {
        return call_modifier_construct((modifier_construct) the_construct);
      }

      if (the_construct instanceof s_expression) {
        return call_s_expression((s_expression) the_construct);
      }

      if (the_construct instanceof block_construct) {
        return call_block_construct((block_construct) the_construct);
      }

      if (the_construct instanceof return_construct) {
        return call_return_construct((return_construct) the_construct);
      }

      if (the_construct instanceof variable_construct) {
        return call_variable_construct((variable_construct) the_construct);
      }

      if (the_construct instanceof procedure_construct) {
        return call_procedure_construct((procedure_construct) the_construct);
      }

      if (the_construct instanceof supertype_construct) {
        return call_supertype_construct((supertype_construct) the_construct);
      }

      if (the_construct instanceof type_construct) {
        return call_type_construct((type_construct) the_construct);
      }

      return call_construct(the_construct);
    }

    public result call_construct(construct the_construct) {
      throw new Error("Unknown construct type for " + the_construct);
    }

    public result call_identifier(identifier the_identifier) {
      return call_construct(the_identifier);
    }

    public result call_operator(operator the_operator) {
      return call_construct(the_operator);
    }

    public result call_string_literal(string_literal the_string_literal) {
      return call_construct(the_string_literal);
    }

    public result call_parameter_construct(parameter_construct the_parameter_construct) {
      return call_construct(the_parameter_construct);
    }

    public result call_modifier_construct(modifier_construct the_modifier_construct) {
      return call_construct(the_modifier_construct);
    }

    public result call_s_expression(s_expression the_s_expression) {
      return call_construct(the_s_expression);
    }

    public result call_block_construct(block_construct the_block_construct) {
      return call_construct(the_block_construct);
    }

    public result call_return_construct(return_construct the_return_construct) {
      return call_construct(the_return_construct);
    }

    public result call_variable_construct(variable_construct the_variable_construct) {
      return call_construct(the_variable_construct);
    }

    public result call_procedure_construct(procedure_construct the_procedure_construct) {
      return call_construct(the_procedure_construct);
    }

    public result call_supertype_construct(supertype_construct the_supertype_construct) {
      return call_construct(the_supertype_construct);
    }

    public result call_type_construct(type_construct the_type_construct) {
      return call_construct(the_type_construct);
    }
  }

  public static class operator implements construct {
    public final operator_type the_operator_type;
    public final source the_source;

    public operator(operator_type the_operator_type, source the_source) {
      this.the_operator_type = the_operator_type;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      return "<operator:" + the_operator_type + ">";
    }
  }

  public static class string_literal implements construct, token {
    public final String value;
    public final String with_quotes;
    public final source the_source;

    public string_literal(String value, String with_quotes, source the_source) {
      this.value = value;
      this.with_quotes = with_quotes;
      this.the_source = the_source;
    }

    @Override
    public token_type type() {
      return token_type.LITERAL;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      return "<string_literal:" + value + ">";
    }
  }

  public enum grouping_type {
    PARENS,
    ANGLE_BRACKETS,
    OPERATOR;
  }

  public static class parameter_construct implements construct {
    public final construct main;
    public final List<construct> parameters;
    public final @Nullable grouping_type grouping;
    public final source the_source;

    public parameter_construct(construct main, List<construct> parameters,
         @Nullable grouping_type grouping, source the_source) {
      this.main = main;
      this.parameters = parameters;
      this.grouping = grouping;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("parameters:<")
          .append("main:").append(main)
          .append(" parameters:").append(fn_display_list(parameters))
          .append(">");
      return result.toString();
    }
  }

  public enum modifier_kind {
    PUBLIC,
    PRIVATE,
    FINAL,
    STATIC,
    OVERRIDE,
    DONT_DESCRIBE,
    NULLABLE;
  }

  public static class modifier_construct implements construct, token {
    public final modifier_kind the_modifier_kind;
    public final source the_source;

    public modifier_construct(modifier_kind the_modifier_kind, source the_source) {
      this.the_modifier_kind = the_modifier_kind;
      this.the_source = the_source;
    }

    @Override
    public token_type type() {
      return token_type.MODIFIER;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("modifier_construct:<")
          .append("modifier:").append(the_modifier_kind)
          .append(" source:").append(the_source)
          .append(">");
      return result.toString();
    }
  }

  public static class s_expression implements construct {
    public final List<construct> parameters;
    public final source the_source;

    public s_expression(List<construct> parameters, source the_source) {
      this.parameters = parameters;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      return "s-expr:" + fn_display_list(parameters);
    }
  }

  public static class block_construct implements construct {
    public final List<construct> statements;
    public final source the_source;

    public block_construct(List<construct> statements, source the_source) {
      this.statements = statements;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      return "block:" + fn_display_list(statements);
    }
  }

  public static class return_construct implements construct {
    public final @Nullable construct expression;
    public final source the_source;

    public return_construct(@Nullable construct expression, source the_source) {
      this.expression = expression;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      return "return: " + expression;
    }
  }

  public static class variable_construct implements construct {
    public final List<modifier_construct> modifiers;
    public final @Nullable construct type;
    public final String name;
    public final @Nullable construct initializer;
    public final source the_source;

    public variable_construct(
        List<modifier_construct> modifiers,
        @Nullable construct type,
        String name,
        @Nullable construct initializer,
        source the_source) {
      this.modifiers = modifiers;
      this.type = type;
      this.name = name;
      this.initializer = initializer;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("variable:<")
          .append(fn_display_list(modifiers))
          .append(" type:").append(type)
          .append(" name:").append(name)
          .append(" init:").append(initializer)
          .append(" source:").append(the_source)
          .append(">");
      return result.toString();
    }
  }

  public static class procedure_construct implements construct {
    public final List<modifier_construct> modifiers;
    public final @Nullable construct return_type;
    public final String name;
    public final List<variable_construct> parameters;
    public final @Nullable construct body;
    public final source the_source;

    public procedure_construct(
        List<modifier_construct> modifiers,
        @Nullable construct return_type,
        String name,
        List<variable_construct> parameters,
        @Nullable construct body,
        source the_source) {
      this.modifiers = modifiers;
      this.return_type = return_type;
      this.name = name;
      this.parameters = parameters;
      this.body = body;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("procedure:<")
          .append(fn_display_list(modifiers))
          .append(" return_type:").append(return_type)
          .append(" name:").append(name)
          .append(" parameters:").append(parameters)
          .append(" body:").append(body)
          .append(" source:").append(the_source)
          .append(">");
      return result.toString();
    }
  }

  public static enum supertype_kind {
    EXTENDS,
    IMPLEMENTS;
  }

  public static class supertype_construct implements construct {
    public final supertype_kind the_supertype_kind;
    public final List<construct> supertypes;
    public final source the_source;

    public supertype_construct(
        supertype_kind the_supertype_kind,
        List<construct> supertypes,
        source the_source) {
      this.the_supertype_kind = the_supertype_kind;
      this.supertypes = supertypes;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }
  }

  public static enum type_kind {
    INTERFACE,
    DATATYPE,
    ENUM,
    CLASS;
  }

  public static class type_construct implements construct {
    public final List<modifier_construct> modifiers;
    public final type_kind the_type_kind;
    public final String name;
    public final List<construct> body;
    public final source the_source;

    public type_construct(
        List<modifier_construct> modifiers,
        type_kind the_type_kind,
        String name,
        List<construct> body,
        source the_source) {
      this.modifiers = modifiers;
      this.the_type_kind = the_type_kind;
      this.name = name;
      this.body = body;
      this.the_source = the_source;
    }

    @Override
    public source the_source() {
      return the_source;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("type_declaration:<")
          .append(fn_display_list(modifiers))
          .append(" type_kind:").append(the_type_kind)
          .append(" name:").append(name)
          .append(" body:").append(fn_display_list(body))
          .append(" source:").append(the_source)
          .append(">");
      return result.toString();
    }
  }

  public static List<token> tokenize(source_text the_source_text) {
    String content = the_source_text.content();
    int index = 0;
    List<token> result = new ArrayList<token>();
    while (index < content.length()) {
      int start = index;
      char prefix = content.charAt(index);
      index += 1;
      source position = new text_position_class(the_source_text, start);
      if (fn_is_identifier_letter(prefix)) {
        while (index < content.length() && fn_is_identifier_letter(content.charAt(index))) {
          index += 1;
        }
        result.add(new identifier(content.substring(start, index), position));
      } else if (fn_is_whitespace(prefix)) {
        while (index < content.length() && fn_is_whitespace(content.charAt(index))) {
          index += 1;
        }
        result.add(new simple_token(token_type.WHITESPACE, position));
      } else if (prefix == '(') {
        result.add(new simple_token(token_type.OPEN, position));
      } else if (prefix == ')') {
        result.add(new simple_token(token_type.CLOSE, position));
      } else if (prefix == '"') {
        char quote = prefix;
        while (index < content.length() &&
               content.charAt(index) != quote &&
               content.charAt(index) != '\n') {
          index += 1;
        }
        if (index == content.length()) {
          report(new notification(notification_type.EOF_IN_STRING_LITERAL, position));
        } else if (content.charAt(index) == '\n') {
          index += 1;
          report(new notification(notification_type.NEWLINE_IN_STRING_LITERAL, position));
        } else {
          assert content.charAt(index) == quote;
          String value = content.substring(start + 1, index);
          String with_quotes = content.substring(start, index + 1);
          index += 1;
          result.add(new string_literal(value, with_quotes, position));
        }
      } else if (prefix == ';') {
        while (index < content.length() && content.charAt(index) != '\n') {
          index += 1;
        }
        result.add(new simple_token(token_type.COMMENT, position));
      } else {
        report(new notification(notification_type.UNRECOGNIZED_CHARACTER, position));
      }
    }
    return result;
  }

  // TODO: separate is_letter() and move to the library
  public static boolean fn_is_identifier_letter(char c) {
    return Character.isLetter(c) || c == '_' || c == '.';
  }

  public static boolean fn_is_whitespace(char c) {
    return Character.isWhitespace(c);
  }

  public static String fn_to_lowercase(String s) {
    return s.toLowerCase();
  }

  public static String fn_display_list(List the_list) {
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < the_list.size(); ++i) {
      if (i > 0) {
        result.append(' ');
      }
      result.append(the_list.get(i).toString());
    }
    result.append(']');

    return result.toString();
  }

  public static void report(notification the_notification) {
    String message = the_notification.type().message();

    @Nullable source deep_source = the_notification.the_source();
    while(deep_source != null) {
      if (deep_source instanceof text_position) {
        text_position position = (text_position) deep_source;
        String content = position.the_source().content();
        int index = position.character_index();
        int line_number = 1;
        for (int i = index - 1; i >= 0; --i) {
          if (content.charAt(i) == '\n') {
            line_number += 1;
          }
        }
        StringBuilder detailed = new StringBuilder();
        detailed.append(position.the_source().name()).append(":");
        detailed.append(line_number).append(": ");
        detailed.append(message).append('\n');

        int start_of_line = index;
        while (start_of_line > 0 && content.charAt(start_of_line - 1) != '\n') {
          start_of_line -= 1;
        }
        int spaces;
        if (index >= content.length() || content.charAt(index) == '\n') {
          detailed.append(content.substring(start_of_line, index));
          detailed.append('\n');
        } else {
          int end_of_line = index;
          while (end_of_line < (content.length() - 1) && content.charAt(end_of_line + 1) != '\n') {
            end_of_line += 1;
          }
          detailed.append(content.substring(start_of_line, end_of_line + 1));
          detailed.append('\n');
        }
        for (int i = 0; i < (index - start_of_line); ++i) {
          detailed.append(' ');
        }
        detailed.append('^');
        // Last newline is added by println().
        message = detailed.toString();
        break;
      }
      if (deep_source instanceof source_text) {
        message = ((source_text) deep_source).name() + ": " + message;
        break;
      }
      deep_source = deep_source.the_source();
    }

    System.err.println(message);
  }

  public interface identifier_processor {
    token process(identifier the_identifier);
  }

  public interface postprocessor {
    @Nullable identifier_processor get_processor(String name);
  }

  public static class modifier_processor implements identifier_processor {
    private final modifier_kind the_modifier_kind;

    public modifier_processor(modifier_kind the_modifier_kind) {
      this.the_modifier_kind = the_modifier_kind;
    }

    @Override
    public token process(identifier the_identifier) {
      return new modifier_construct(the_modifier_kind, the_identifier);
    }
  }

  public static class common_postprocessor implements postprocessor {
    private Map<String, identifier_processor> processors;

    public common_postprocessor() {
      processors = new HashMap<String, identifier_processor>();
    }

    public void add(modifier_kind the_modifier_kind) {
      processors.put(fn_to_lowercase(the_modifier_kind.name()),
          new modifier_processor(the_modifier_kind));
    }

    public @Nullable identifier_processor get_processor(String name) {
      return processors.get(name);
    }
  }

  public static List<token> postprocess(List<token> tokens, postprocessor the_postprocessor) {
    List<token> result = new ArrayList<token>();

    for (token the_token : tokens) {
      if (the_token.type() == token_type.WHITESPACE ||
          the_token.type() == token_type.COMMENT) {
        continue;
      }
      if (the_token instanceof identifier) {
        identifier the_identifier = (identifier) the_token;
        @Nullable identifier_processor the_identifier_processor =
            the_postprocessor.get_processor(the_identifier.name());
        if (the_identifier_processor != null) {
          result.add(the_identifier_processor.process(the_identifier));
          continue;
        }
      }
      result.add(the_token);
    }

    return result;
  }

  private static final String NULLABLE_NAME = "nullable";

  private static final String LIST_NAME = "list";

  private static int parse_sublist(List<token> tokens, int start, List<construct> result,
      parser_context context) {
    int index = start;
    while (index < tokens.size()) {
      token the_token = tokens.get(index);
      index += 1;
      if (the_token instanceof construct) {
        result.add((construct) the_token);
      } else if (the_token.type() == token_type.OPEN) {
        List<construct> parameters = new ArrayList<construct>();
        int end = parse_sublist(tokens, index, parameters, context);
        if (end >= tokens.size()) {
          report(new notification(notification_type.CLOSE_PAREN_NOT_FOUND, the_token));
        } else if (tokens.get(end).type() != token_type.CLOSE) {
          report(new notification(notification_type.CLOSE_PAREN_NOT_FOUND, tokens.get(end)));
        } else {
          end += 1;
        }
        index = end;
        if (parameters.size() > 0 &&
            parameters.get(0) instanceof identifier) {
          identifier name_identifier = (identifier) parameters.get(0);
          String name = name_identifier.name();
          List<construct> rest = parameters.subList(1, parameters.size());
          @Nullable special_parser the_parser = context.get_parser(name);
          if (the_parser != null) {
            @Nullable construct parsed = the_parser.parse(rest);
            if (parsed != null) {
              result.add(parsed);
            } else {
              report(new notification(notification_type.PARSE_ERROR, name_identifier));
            }
            continue;
          }
          result.add(new parameter_construct(name_identifier, rest, grouping_type.PARENS,
              the_token));
        } else {
          result.add(new s_expression(parameters, the_token));
        }
      } else if (the_token.type() == token_type.CLOSE) {
        return index - 1;
      } else {
        report(new notification(notification_type.PARSE_ERROR, the_token));
      }
    }

    return index;
  }

  public interface special_parser {
    @Nullable construct parse(List<construct> parameters);
  }

  public interface parser_context {
    @Nullable special_parser get_parser(String name);
  }

  public static final special_parser VARIABLE_PARSER = new special_parser() {
    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.size() < 2 || parameters.size() > 4) {
        return null;
      }


      List<modifier_construct> modifiers;
      int type_index;
      if (parameters.size() == 2) {
        modifiers = new ArrayList<modifier_construct>();
        type_index = 0;
      } else {
        modifiers = parse_modifiers(parameters.get(0));
        type_index = 1;
      }

      construct type = parameters.get(type_index);

      construct name_construct = parameters.get(type_index + 1);
      if (!(name_construct instanceof identifier)) {
        return null;
      }
      String name = ((identifier) name_construct).name();

      @Nullable construct initializer = (parameters.size() == type_index + 3) ?
          parameters.get(type_index + 2) : null;

      return new variable_construct(modifiers, type, name, initializer, name_construct);
    }
  };

  public static class type_parser implements special_parser {
    public final type_kind the_type_kind;

    public type_parser(type_kind the_type_kind) {
      this.the_type_kind = the_type_kind;
    }

    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.size() < 1) {
        return null;
      }
      if (!(parameters.get(0) instanceof identifier)) {
        return null;
      }

      String name = ((identifier) parameters.get(0)).name();
      List<construct> body = parameters.subList(1, parameters.size());
      source the_source = parameters.get(0);

      return new type_construct(new ArrayList<modifier_construct>(), the_type_kind, name, body,
          the_source);
    }
  };

  public static class supertype_parser implements special_parser {
    public final supertype_kind the_supertype_kind;

    public supertype_parser(supertype_kind the_supertype_kind) {
      this.the_supertype_kind = the_supertype_kind;
    }

    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.isEmpty()) {
        return null;
      }

      // TODO: should this be a parameter to parse()?
      source the_source = parameters.get(0);
      return new supertype_construct(the_supertype_kind, parameters, the_source);
    }
  };

  private static List<modifier_construct> parse_modifiers(construct the_construct) {
    // TODO(dynin): implement actual modifier parsing.
    if (!(the_construct instanceof s_expression)) {
      report(new notification(notification_type.PARSE_ERROR, the_construct));
    }

    List<construct> parameters = ((s_expression) the_construct).parameters;
    List<modifier_construct> result = new ArrayList<modifier_construct>();
    for (construct parameter : parameters) {
      if (parameter instanceof modifier_construct) {
        result.add((modifier_construct) parameter);
      } else {
        report(new notification(notification_type.MODIFIER_EXPECTED, parameter));
      }
    }
    return result;
  }

  public static class common_context implements parser_context {
    private Map<String, special_parser> parsers;

    public common_context() {
      parsers = new HashMap<String, special_parser>();
      parsers.put("variable", VARIABLE_PARSER);
      parsers.put("datatype", new type_parser(type_kind.DATATYPE));
      parsers.put("interface", new type_parser(type_kind.INTERFACE));
      parsers.put("enum", new type_parser(type_kind.ENUM));
      parsers.put("class", new type_parser(type_kind.CLASS));
      parsers.put("extends", new supertype_parser(supertype_kind.EXTENDS));
      parsers.put("implements", new supertype_parser(supertype_kind.IMPLEMENTS));
    }

    @Override
    public @Nullable special_parser get_parser(String name) {
      return parsers.get(name);
    }
  }

  public static List<construct> parse(List<token> tokens, parser_context context) {
    List<construct> result = new ArrayList<construct>();
    int consumed = parse_sublist(tokens, 0, result, context);
    if (consumed < tokens.size()) {
      report(new notification(notification_type.PARSE_ERROR, tokens.get(consumed)));
    }
    return result;
  }

  public static final text COMMA = new text_string(",");
  public static final text OPEN_PAREN = new text_string("(");
  public static final text CLOSE_PAREN = new text_string(")");
  public static final text LESS_THAN = new text_string("<");
  public static final text GREATER_THAN = new text_string(">");
  public static final text OPEN_BRACE = new text_string("{");
  public static final text CLOSE_BRACE = new text_string("}");
  public static final text SEMICOLON = new text_string(";");
  public static final text RETURN_KEYWORD = new text_string("return");

  public static class base_printer extends construct_dispatch<text> {

    function<text, variable_construct> param_printer = new function<text, variable_construct>() {
      @Override
      public text call(variable_construct the_variable_construct) {
        return print_variable(the_variable_construct);
      }
    };

    function<text, construct> statement_printer = new function<text, construct>() {
      @Override
      public text call(construct the_construct) {
        return join_text(print(the_construct), SEMICOLON, NEWLINE);
      }
    };

    public text print(construct the_construct) {
      return call(the_construct);
    }

    public text print_all(List<? extends construct> constructs) {
      return new text_list(map(constructs, this));
    }

    @Override
    public text call_identifier(identifier the_identifier) {
      return new text_string(the_identifier.name());
    }

    @Override
    public text call_string_literal(string_literal the_string_literal) {
      return new text_string(the_string_literal.with_quotes);
    }

    @Override
    public text call_parameter_construct(parameter_construct the_parameter_construct) {
      if (the_parameter_construct.main instanceof operator) {
        return print_operator((operator) the_parameter_construct.main,
            the_parameter_construct.parameters);
      }

      text main = print(the_parameter_construct.main);
      text parameters = fold_with_comma(the_parameter_construct.parameters, this);

      if (the_parameter_construct.grouping == grouping_type.ANGLE_BRACKETS) {
        return join_text(main, LESS_THAN, parameters, GREATER_THAN);
      } else {
        return join_text(main, OPEN_PAREN, parameters, CLOSE_PAREN);
      }
    }

    public text print_operator(operator the_operator, List<construct> arguments) {
      assert arguments.size() == 2;
      operator_type the_operator_type = the_operator.the_operator_type;
      text operator_text = new text_string(the_operator_type.symbol());
      if (the_operator_type != operator_type.DOT) {
        operator_text = join_text(SPACE, operator_text, SPACE);
      }
      return join_text(print(arguments.get(0)), operator_text, print(arguments.get(1)));
    }

    @Override
    public text call_modifier_construct(modifier_construct the_modifier_construct) {
      return new text_string(fn_to_lowercase(the_modifier_construct.the_modifier_kind.toString()));
    }

    @Override
    public text call_s_expression(s_expression the_s_expression) {
      return join_text(OPEN_PAREN, join_text(map(the_s_expression.parameters, this), SPACE),
        CLOSE_PAREN);
    }

    @Override
    public text call_block_construct(block_construct the_block_construct) {
      return join_text(OPEN_BRACE, NEWLINE,
          indent(new text_list(map(the_block_construct.statements, statement_printer))),
          CLOSE_BRACE);
    }

    @Override
    public text call_return_construct(return_construct the_return_construct) {
      if (the_return_construct.expression == null) {
        return RETURN_KEYWORD;
      } else {
        return join_text(RETURN_KEYWORD, SPACE, print(the_return_construct.expression));
      }
    }

    protected text print_variable(variable_construct the_variable_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_with_space(the_variable_construct.modifiers));
      result.add(print(the_variable_construct.type));
      result.add(SPACE);
      result.add(new text_string(the_variable_construct.name));
      return new text_list(result);
    }

    @Override
    public text call_variable_construct(variable_construct the_variable_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_variable(the_variable_construct));
      result.add(SEMICOLON);
      result.add(NEWLINE);
      return new text_list(result);
    }

    @Override
    public text call_procedure_construct(procedure_construct the_procedure_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_with_space(the_procedure_construct.modifiers));
      if (the_procedure_construct.return_type != null) {
        result.add(print(the_procedure_construct.return_type));
        result.add(SPACE);
      }
      result.add(new text_string(the_procedure_construct.name));
      result.add(OPEN_PAREN);

      result.add(fold_with_comma(the_procedure_construct.parameters, param_printer));
      result.add(CLOSE_PAREN);
      if (the_procedure_construct.body == null) {
        result.add(SEMICOLON);
      } else {
        // TODO: relax this constraint.
        assert the_procedure_construct.body instanceof block_construct;
        result.add(SPACE);
        result.add(print(the_procedure_construct.body));
      }
      result.add(NEWLINE);
      return new text_list(result);
    }

    @Override
    public text call_supertype_construct(supertype_construct the_supertype_construct) {
      List<text> result = new ArrayList<text>();
      result.add(new text_string(fn_to_lowercase(
          the_supertype_construct.the_supertype_kind.toString())));
      result.add(SPACE);
      result.add(fold_with_comma(the_supertype_construct.supertypes, this));
      result.add(SEMICOLON);
      result.add(NEWLINE);
      return new text_list(result);
    }

    @Override
    public text call_type_construct(type_construct the_type_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_with_space(the_type_construct.modifiers));
      result.add(new text_string(fn_to_lowercase(the_type_construct.the_type_kind.toString())));
      result.add(SPACE);
      result.add(new text_string(the_type_construct.name));
      result.add(SPACE);
      result.add(OPEN_BRACE);
      result.add(NEWLINE);
      result.add(indent(print_all(the_type_construct.body)));
      result.add(CLOSE_BRACE);
      result.add(NEWLINE);
      return new text_list(result);
    }

    // TODO(dynin): use map().
    protected text print_with_space(List<? extends construct> constructs) {
      List<text> result = new ArrayList<text>();
      for (construct the_construct : constructs) {
        result.add(print(the_construct));
        result.add(SPACE);
      }
      return new text_list(result);
    }

    // TODO(dynin): make generic.
    protected <C> text fold_with_comma(List<C> constructs, function<text, C> printer) {
      if (constructs.size() == 0) {
        return EMPTY_TEXT;
      } else if (constructs.size() == 1) {
        return printer.call(constructs.get(0));
      } else {
        List<text> result = new ArrayList<text>();
        for (int i = 0; i < constructs.size(); ++i) {
          if (i > 0) {
            result.add(COMMA);
            result.add(SPACE);
          }
          result.add(printer.call(constructs.get(i)));
        }
        return new text_list(result);
      }
    }
  }

  public static predicate<construct> is_supertype = new predicate<construct>() {
    @Override
    public boolean call(construct the_construct) {
      return the_construct instanceof supertype_construct;
    }
  };
  public static predicate<construct> is_not_supertype = negate_predicate(is_supertype);

  public static predicate<construct> is_enum_declaration = new predicate<construct>() {
    @Override
    public boolean call(construct the_construct) {
      return the_construct instanceof identifier ||
             the_construct instanceof parameter_construct;
    }
  };

  public static predicate<construct> is_not_enum_declaration =
      negate_predicate(is_enum_declaration);

  public static class java_printer extends base_printer {

    @Override
    public text call_modifier_construct(modifier_construct the_modifier_construct) {
      if (is_java_annotation(the_modifier_construct.the_modifier_kind)) {
        String name = the_modifier_construct.the_modifier_kind.name();
        String annotation_name = "@" + name.charAt(0) + fn_to_lowercase(name.substring(1));
        return new text_string(annotation_name);
      } else {
        return super.call_modifier_construct(the_modifier_construct);
      }
    }

    @Override
    public text call_supertype_construct(supertype_construct the_supertype_construct) {
      List<text> result = new ArrayList<text>();
      result.add(new text_string(fn_to_lowercase(
          the_supertype_construct.the_supertype_kind.toString())));
      result.add(SPACE);
      result.add(fold_with_comma(the_supertype_construct.supertypes, this));
      // No trailing semicolon or newline.
      return new text_list(result);
    }

    @Override
    public text call_type_construct(type_construct the_type_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_with_space(the_type_construct.modifiers));
      result.add(new text_string(fn_to_lowercase(the_type_construct.the_type_kind.toString())));
      result.add(SPACE);
      result.add(new text_string(the_type_construct.name));
      result.add(SPACE);
      List<construct> supertypes = filter(the_type_construct.body, is_supertype);
      result.add(print_with_space(supertypes));
      result.add(OPEN_BRACE);
      result.add(NEWLINE);
      List<construct> filtered_body = filter(the_type_construct.body, is_not_supertype);

      List<text> body = new ArrayList<text>();
      if (the_type_construct.the_type_kind == type_kind.ENUM) {
        List<construct> enum_declarations = filter(filtered_body, is_enum_declaration);
        assert !enum_declarations.isEmpty();
        for (int i = 0; i < enum_declarations.size(); ++i) {
          body.add(print(enum_declarations.get(i)));
          body.add(i < enum_declarations.size() - 1 ? COMMA : SEMICOLON);
          body.add(NEWLINE);
        }
        filtered_body = filter(filtered_body, is_not_enum_declaration);
      }
      body.add(print_all(filtered_body));

      result.add(indent(new text_list(body)));
      result.add(CLOSE_BRACE);
      result.add(NEWLINE);
      return new text_list(result);
    }

    public boolean is_java_annotation(modifier_kind the_modifier_kind) {
      return the_modifier_kind == modifier_kind.OVERRIDE ||
             the_modifier_kind == modifier_kind.NULLABLE;
    }
  }

  public static class base_transform extends construct_dispatch<Object> {

    public construct transform(construct the_construct) {
      return (construct) call(the_construct);
    }

    public List<construct> transform_all(List<construct> constructs) {
      // TODO: rewrite using map()
      List<construct> result = new ArrayList<construct>();
      for (construct the_construct : constructs) {
        Object transformed = call(the_construct);
        if (transformed instanceof List) {
          @SuppressWarnings("unchecked")
          List<construct> transformedList = (List<construct>) transformed;
          result.addAll(transformedList);
        } else {
          result.add((construct) transformed);
        }
      }
      return result;
    }

    @Override
    public construct call_construct(construct the_construct) {
      return the_construct;
    }

    @Override
    public Object call_type_construct(type_construct the_type_construct) {
      return new type_construct(the_type_construct.modifiers,
          the_type_construct.the_type_kind,
          the_type_construct.name,
          transform_all(the_type_construct.body),
          the_type_construct);
    }
  }

  public static class to_java_transform extends base_transform {

    @Override
    public construct call_parameter_construct(parameter_construct the_parameter_construct) {
      source the_source = the_parameter_construct;

      construct main = the_parameter_construct.main;
      construct transformed_main = transform(main);

      List<construct> parameters = new ArrayList<construct>();
      for (construct the_parameter : the_parameter_construct.parameters) {
        Object transformed = call(the_parameter);
        if (transformed instanceof construct) {
          parameters.add((construct) transformed);
        } else {
          unexpected("Parameter transform error: " + the_parameter);
        }
      }

      grouping_type grouping = the_parameter_construct.grouping;
      if (main instanceof identifier &&
          ((identifier) main).name().equals(LIST_NAME)) {
        grouping = grouping_type.ANGLE_BRACKETS;
      }

      return new parameter_construct(transformed_main, parameters, grouping, the_source);
    }

    @Override
    public variable_construct call_variable_construct(variable_construct the_variable_construct) {
      assert the_variable_construct.type != null;
      source the_source = the_variable_construct;
      List<modifier_construct> modifiers = the_variable_construct.modifiers;
      construct type = transform(the_variable_construct.type);
      if (is_nullable(type)) {
        type = strip_nullable(type);
        modifiers = prepend_modifier(modifier_kind.NULLABLE, modifiers, the_source);
      }
      // TODO: add variable_construct.has_initializer
      @Nullable construct initializer = the_variable_construct.initializer;
      if (initializer != null) {
        initializer = transform(initializer);
      }
      return new variable_construct(modifiers, type, the_variable_construct.name, initializer,
          the_source);
    }

    private static List<modifier_construct> prepend_modifier(modifier_kind the_modifier_kind,
        List<modifier_construct> modifiers, source the_source) {
      List<modifier_construct> result = new ArrayList<modifier_construct>();
      result.add(new modifier_construct(the_modifier_kind, the_source));
      result.addAll(modifiers);
      return result;
    }

    private static List<modifier_construct> filter_modifier(final modifier_kind the_modifier_kind,
        List<modifier_construct> modifiers) {
      return filter(modifiers, new predicate<modifier_construct>() {
        @Override
        public boolean call(modifier_construct the_modifier_construct) {
          return the_modifier_construct.the_modifier_kind != the_modifier_kind;
        }
      });
    }

    private static String join_identifier(String first, String second) {
      return first + '_' + second;
    }

    private static parameter_construct make_operator(operator_type the_operator_type,
        construct first_argument, construct second_argument, source the_source) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(first_argument);
      arguments.add(second_argument);
      return new parameter_construct(new operator(the_operator_type, the_source), arguments,
          grouping_type.OPERATOR, the_source);
    }

    @Override
    public Object call_type_construct(type_construct the_type_construct) {

      type_kind the_type_kind = the_type_construct.the_type_kind;

      boolean declare_interface =
          the_type_kind == type_kind.INTERFACE ||
          the_type_kind == type_kind.DATATYPE;

      boolean declare_implementation =
          the_type_kind == type_kind.DATATYPE ||
          the_type_kind == type_kind.ENUM ||
          the_type_kind == type_kind.CLASS;

      boolean declare_enum = the_type_kind == type_kind.ENUM;

      source the_source = the_type_construct;

      String interface_name = the_type_construct.name;
      String implementation_name = declare_interface ? join_identifier(interface_name, "class") :
          interface_name;

      List<construct> interface_body = new ArrayList<construct>();
      List<construct> implementation_body = new ArrayList<construct>();

      if (declare_interface && declare_implementation) {
        List<construct> super_interface = new ArrayList<construct>();
        super_interface.add(new identifier(interface_name, the_source));
        implementation_body.add(new supertype_construct(supertype_kind.IMPLEMENTS, super_interface,
            the_source));
      }
      List<variable_construct> ctor_parameters = new ArrayList<variable_construct>();
      List<construct> ctor_statements = new ArrayList<construct>();
      List<construct> accessor_functions = new ArrayList<construct>();

      boolean generate_description = false;
      List<String> describe_fields = new ArrayList<String>();

      for (construct the_construct : the_type_construct.body) {
        if (the_construct instanceof supertype_construct) {
          // TODO: implement supertype transformation.
          supertype_construct the_supertype_construct =
              (supertype_construct) transform(the_construct);
          if (declare_interface) {
            interface_body.add(the_supertype_construct);
          } else {
            implementation_body.add(the_supertype_construct);
          }
          if (has_describable(the_supertype_construct.supertypes)) {
            generate_description = true;
          }
        } else if (the_construct instanceof variable_construct) {
          variable_construct the_variable_construct =
              call_variable_construct((variable_construct) the_construct);
          List<modifier_construct> modifiers = the_variable_construct.modifiers;

          boolean has_override = has_modifier(the_variable_construct.modifiers,
              modifier_kind.OVERRIDE);
          if (has_override) {
            modifiers = filter_modifier(modifier_kind.OVERRIDE, modifiers);
          }
          boolean has_dont_describe = has_modifier(the_variable_construct.modifiers,
              modifier_kind.DONT_DESCRIBE);
          if (has_dont_describe) {
            modifiers = filter_modifier(modifier_kind.DONT_DESCRIBE, modifiers);
          }

          construct type = the_variable_construct.type;
          String name = the_variable_construct.name;

          // Add accessor declaration to the interface
          if (declare_interface) {
            interface_body.add(new procedure_construct(modifiers,
                type, name, new ArrayList<variable_construct>(), null, the_source));
          }

          if (declare_implementation) {
            boolean has_initializer = the_variable_construct.initializer != null;

            if (!has_initializer) {
              // Add instance variable
              List<modifier_construct> instance_variable_modifiers =
                  prepend_modifier(modifier_kind.PRIVATE,
                      prepend_modifier(modifier_kind.FINAL, modifiers, the_source), the_source);
              implementation_body.add(new variable_construct(instance_variable_modifiers, type, name, null,
                  the_source));

              // Add constructor parameter
              ctor_parameters.add(new variable_construct(modifiers, type, name, null, the_source));
              identifier variable_identifier = new identifier(name, the_source);
              identifier this_identifier = new identifier("this", the_source);
              construct this_access = make_operator(operator_type.DOT, this_identifier,
                  variable_identifier, the_source);
              construct assignment = make_operator(operator_type.ASSIGN, this_access,
                  variable_identifier, the_source);
              ctor_statements.add(assignment);

              // Add field description
              if (!has_dont_describe) {
                describe_fields.add(name);
              }
            }

            // Add accessor function
            List<modifier_construct> accessor_modifiers =
                prepend_modifier(modifier_kind.PUBLIC, modifiers, the_source);
            if (declare_interface || has_override) {
                accessor_modifiers = prepend_modifier(modifier_kind.OVERRIDE, accessor_modifiers,
                    the_source);
            }

            construct return_expression = has_initializer ?  the_variable_construct.initializer :
                new identifier(name, the_source);
            construct accessor_return = new return_construct(return_expression, the_source);
            List<construct> accessor_statements = new ArrayList<construct>();
            accessor_statements.add(accessor_return);
            construct accessor_body = new block_construct(accessor_statements, the_source);
            procedure_construct accessor = new procedure_construct(accessor_modifiers,
                type, name, new ArrayList<variable_construct>(), accessor_body, the_source);
            accessor_functions.add(accessor);
          }
        } else if (declare_enum && is_enum_declaration.call(the_construct)) {
          implementation_body.add(the_construct);
        } else {
          // TODO: handle other constructs.
          unexpected("In type declaration: " + the_construct);
        }
      }

      List<construct> result = new ArrayList<construct>();

      if (declare_interface) {
        type_construct interface_type =
            new type_construct(the_type_construct.modifiers,
                type_kind.INTERFACE,
                interface_name,
                interface_body,
                the_source);
        result.add(interface_type);
      }

      if (declare_implementation) {
        if (!ctor_parameters.isEmpty()) {
          List<modifier_construct> ctor_modifiers = new ArrayList<modifier_construct>();
          if (!declare_enum) {
            ctor_modifiers.add(new modifier_construct(modifier_kind.PUBLIC, the_source));
          }
          block_construct ctor_body = new block_construct(ctor_statements, the_source);
          procedure_construct ctor_procedure =
              new procedure_construct(
                  ctor_modifiers,
                  null,
                  implementation_name,
                  ctor_parameters,
                  ctor_body,
                  the_source);
          implementation_body.add(ctor_procedure);
        }

        implementation_body.addAll(accessor_functions);

        if (generate_description && !declare_enum && !describe_fields.isEmpty()) {
          implementation_body.add(generate_description(implementation_name, describe_fields,
              the_source));
        }

        type_construct implementation_type =
            new type_construct(the_type_construct.modifiers,
                declare_enum ? type_kind.ENUM : type_kind.CLASS,
                implementation_name,
                implementation_body,
                the_source);
        result.add(implementation_type);
      }

      return result;
    }

    private procedure_construct generate_description(String type_name, List<String> fields,
        final source the_source) {

      List<construct> join_arguments = new ArrayList<construct>();
      join_arguments.add(make_literal(type_name, the_source));
      join_arguments.add(new identifier("START_OBJECT", the_source));

      if (fields.size() != 1) {
        List<construct> field_calls = map(fields, new function<construct, String>() {
          @Override
          public construct call(String name) {
            construct name_literal = make_literal(name, the_source);
            construct name_identifier = new identifier(name, the_source);
            return call_function2("field_is", name_literal, name_identifier, the_source);
          }
        });
        construct indent_call = new parameter_construct(new identifier("indent", the_source),
            field_calls, grouping_type.PARENS, the_source);

        join_arguments.add(new identifier("NEWLINE", the_source));
        join_arguments.add(indent_call);
      } else {
        construct name_identifier = new identifier(fields.get(0), the_source);

        join_arguments.add(new identifier("SPACE", the_source));
        join_arguments.add(call_function1("describe", name_identifier, the_source));
        join_arguments.add(new identifier("SPACE", the_source));
      }

      join_arguments.add(new identifier("END_OBJECT", the_source));
      construct join_call = new parameter_construct(new identifier("join_fragments", the_source),
          join_arguments, grouping_type.PARENS, the_source);

      construct description_return = new return_construct(join_call, the_source);
      List<construct> description_statements = new ArrayList<construct>();
      description_statements.add(description_return);
      block_construct description_body = new block_construct(description_statements, the_source);

      List<modifier_construct> modifiers = new ArrayList<modifier_construct>();
      modifiers.add(new modifier_construct(modifier_kind.OVERRIDE, the_source));
      modifiers.add(new modifier_construct(modifier_kind.PUBLIC, the_source));
      return new procedure_construct(
          modifiers,
          new identifier("text", the_source),
          "description",
          new ArrayList<variable_construct>(),
          description_body,
          the_source);
    }

    public static construct make_literal(String value, source the_source) {
      // TODO: escape properly.
      return new string_literal(value, "\"" + value + "\"", the_source);
    }

    private boolean has_describable(List<construct> constructs) {
      for (construct the_construct : constructs) {
        if (the_construct instanceof identifier &&
            ((identifier) the_construct).name().equals("describable")) {
          return true;
        }
      }
      return false;
    }

    public static construct call_function1(String name, construct argument0, source the_source) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(argument0);
      return new parameter_construct(new identifier(name, the_source),
          arguments, grouping_type.PARENS, the_source);
    }

    public static construct call_function2(String name, construct argument0, construct argument1,
        source the_source) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(argument0);
      arguments.add(argument1);
      return new parameter_construct(new identifier(name, the_source),
          arguments, grouping_type.PARENS, the_source);
    }

    public construct call_identifier(identifier the_identifier) {
      String name = the_identifier.name();
      if (name.equals("string")) {
        return new identifier("String", the_identifier);
      } else if (name.equals(LIST_NAME)) {
        return new identifier("List", the_identifier);
      } else {
        return the_identifier;
      }
    }

    private boolean is_nullable(construct the_construct) {
      if (the_construct instanceof parameter_construct) {
        construct main = ((parameter_construct) the_construct).main;
        return main instanceof identifier &&
               ((identifier) main).name().equals(NULLABLE_NAME);
      }
      return false;
    }

    private construct strip_nullable(construct the_construct) {
      assert is_nullable(the_construct);
      List<construct> parameters = ((parameter_construct) the_construct).parameters;
      // TODO: validate number of parameters in is_nullable()?
      assert parameters.size() == 1;
      return parameters.get(0);
    }

    // TODO: needs to be rewritte using has() and pushed to the library
    private boolean has_modifier(List<modifier_construct> modifiers,
        modifier_kind the_modifier_kind) {
      for (modifier_construct modifier : modifiers) {
        if (modifier.the_modifier_kind == the_modifier_kind) {
          return true;
        }
      }
      return false;
    }
  }

  private static void unexpected(String message) {
    System.err.println("Unexpected: " + message);
    System.exit(1);
  }

  private static final boolean DEBUG_TOKENIZER = false;

  private static final boolean DEBUG_PARSER = false;

  public static postprocessor init_postprocessor() {
    common_postprocessor result = new common_postprocessor();
    result.add(modifier_kind.PUBLIC);
    result.add(modifier_kind.OVERRIDE);
    result.add(modifier_kind.DONT_DESCRIBE);
    return result;
  }

  public static void main(String[] args) {
    String file_name = args[0];
    String file_content = "";

    try {
      file_content = read_file(file_name);
    } catch (IOException e) {
      System.err.println("Can't read " + file_name);
      System.exit(1);
    }

    source_text the_source = new source_text_class(file_name, file_content);
    List<token> tokens = postprocess(tokenize(the_source), init_postprocessor());
    if (DEBUG_TOKENIZER) {
      System.out.println(render_text(describe(tokens)));
    }

    List<construct> constructs = parse(tokens, new common_context());
    if (DEBUG_PARSER) {
      for (construct the_construct : constructs) {
        System.out.println(the_construct.toString());
      }
    }

    constructs = new to_java_transform().transform_all(constructs);

    System.out.print(render_text(new java_printer().print_all(constructs)));
  }

  private static String read_file(String file_name) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(file_name));
    return new String(encoded, StandardCharsets.UTF_8);
  }
}

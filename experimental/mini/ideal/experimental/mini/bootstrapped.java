/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.experimental.mini;

import static ideal.experimental.mini.library.*;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Autogenerated code, do not edit.
 */
public interface bootstrapped {
  interface text {
  }
  class text_string implements text {
    private final String value;
    public text_string(String value) {
      this.value = value;
    }
    public String value() {
      return value;
    }
  }
  class indented_text implements text {
    private final text inside;
    public indented_text(text inside) {
      this.inside = inside;
    }
    public text inside() {
      return inside;
    }
  }
  class text_list implements text {
    private final List<text> texts;
    public text_list(List<text> texts) {
      this.texts = texts;
    }
    public List<text> texts() {
      return texts;
    }
  }
  interface describable {
    text description();
  }
  interface source extends describable {
    @Nullable source the_source();
  }
  interface source_text extends source {
    String name();
    String content();
    @Nullable source the_source();
  }
  class source_text_class implements source_text {
    private final String name;
    private final String content;
    public source_text_class(String name, String content) {
      this.name = name;
      this.content = content;
    }
    @Override public String name() {
      return name;
    }
    @Override public String content() {
      return content;
    }
    @Override public @Nullable source the_source() {
      return null;
    }
    @Override public text description() {
      return join_fragments("source_text_class", START_OBJECT, SPACE, describe(name), SPACE, END_OBJECT);
    }
  }
  interface text_position extends source {
    source_text the_source();
    int character_index();
  }
  class text_position_class implements text_position {
    private final source_text the_source;
    private final int character_index;
    public text_position_class(source_text the_source, int character_index) {
      this.the_source = the_source;
      this.character_index = character_index;
    }
    @Override public source_text the_source() {
      return the_source;
    }
    @Override public int character_index() {
      return character_index;
    }
    @Override public text description() {
      return join_fragments("text_position_class", START_OBJECT, NEWLINE, indent(field_is("the_source", the_source), field_is("character_index", character_index)), END_OBJECT);
    }
  }
  class builtin_source implements source {
    public static final builtin_source instance = new builtin_source();
    @Override public @Nullable source the_source() {
      return null;
    }
    @Override public text description() {
      return new text_string("builtin_source");
    }
  }
  interface token_type extends describable {
  }
  enum core_token_type implements token_type {
    IDENTIFIER,
    LITERAL,
    MODIFIER,
    OPERATOR,
    WHITESPACE,
    COMMENT;
    @Override public text description() {
      return new text_string(name());
    }
  }
  enum punctuation implements token_type {
    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    DOT(".");
    private final String symbol;
    punctuation(String symbol) {
      this.symbol = symbol;
    }
    public String symbol() {
      return symbol;
    }
    @Override public text description() {
      return join_fragments("punctuation", START_OBJECT, SPACE, describe(symbol), SPACE, END_OBJECT);
    }
  }
  interface token extends source {
    token_type the_token_type();
  }
  class simple_token implements token {
    private final token_type the_token_type;
    private final source the_source;
    public simple_token(token_type the_token_type, source the_source) {
      this.the_token_type = the_token_type;
      this.the_source = the_source;
    }
    @Override public token_type the_token_type() {
      return the_token_type;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("simple_token", START_OBJECT, NEWLINE, indent(field_is("the_token_type", the_token_type), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  interface construct extends source {
  }
  class identifier implements token, construct {
    private final String name;
    private final source the_source;
    public identifier(String name, source the_source) {
      this.name = name;
      this.the_source = the_source;
    }
    public String name() {
      return name;
    }
    @Override public token_type the_token_type() {
      return core_token_type.IDENTIFIER;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("identifier", START_OBJECT, NEWLINE, indent(field_is("name", name), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  enum operator_type {
    DOT("."),
    ASSIGN("="),
    NEW("new"),
    IS("is"),
    AS("as");
    private final String symbol;
    operator_type(String symbol) {
      this.symbol = symbol;
    }
    public String symbol() {
      return symbol;
    }
  }
  class operator implements token, construct {
    private final operator_type the_operator_type;
    private final source the_source;
    public operator(operator_type the_operator_type, source the_source) {
      this.the_operator_type = the_operator_type;
      this.the_source = the_source;
    }
    public operator_type the_operator_type() {
      return the_operator_type;
    }
    @Override public token_type the_token_type() {
      return core_token_type.OPERATOR;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("operator", START_OBJECT, NEWLINE, indent(field_is("the_operator_type", the_operator_type), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class string_literal implements token, construct, value_action {
    private final String value;
    private final @Nullable String with_quotes;
    private final source the_source;
    public string_literal(String value, @Nullable String with_quotes, source the_source) {
      this.value = value;
      this.with_quotes = with_quotes;
      this.the_source = the_source;
    }
    public String value() {
      return value;
    }
    public @Nullable String with_quotes() {
      return with_quotes;
    }
    @Override public token_type the_token_type() {
      return core_token_type.LITERAL;
    }
    @Override public type result() {
      return core_type.STRING;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("string_literal", START_OBJECT, NEWLINE, indent(field_is("value", value), field_is("with_quotes", with_quotes), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  enum grouping_type {
    PARENS,
    ANGLE_BRACKETS,
    OPERATOR;
  }
  class parameter_construct implements construct {
    private final construct main;
    private final List<construct> parameters;
    private final @Nullable grouping_type grouping;
    private final source the_source;
    public parameter_construct(construct main, List<construct> parameters, @Nullable grouping_type grouping, source the_source) {
      this.main = main;
      this.parameters = parameters;
      this.grouping = grouping;
      this.the_source = the_source;
    }
    public construct main() {
      return main;
    }
    public List<construct> parameters() {
      return parameters;
    }
    public @Nullable grouping_type grouping() {
      return grouping;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("parameter_construct", START_OBJECT, NEWLINE, indent(field_is("main", main), field_is("parameters", parameters), field_is("grouping", grouping), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  enum modifier_kind {
    PUBLIC,
    PRIVATE,
    FINAL,
    STATIC,
    ABSTRACT,
    OVERRIDE,
    DONT_DESCRIBE,
    NULLABLE;
  }
  class modifier_construct implements token, construct {
    private final modifier_kind the_modifier_kind;
    private final source the_source;
    public modifier_construct(modifier_kind the_modifier_kind, source the_source) {
      this.the_modifier_kind = the_modifier_kind;
      this.the_source = the_source;
    }
    public modifier_kind the_modifier_kind() {
      return the_modifier_kind;
    }
    @Override public token_type the_token_type() {
      return core_token_type.MODIFIER;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("modifier_construct", START_OBJECT, NEWLINE, indent(field_is("the_modifier_kind", the_modifier_kind), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class s_expression implements construct {
    private final List<construct> parameters;
    private final source the_source;
    public s_expression(List<construct> parameters, source the_source) {
      this.parameters = parameters;
      this.the_source = the_source;
    }
    public List<construct> parameters() {
      return parameters;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("s_expression", START_OBJECT, NEWLINE, indent(field_is("parameters", parameters), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class block_construct implements construct {
    private final List<construct> statements;
    private final source the_source;
    public block_construct(List<construct> statements, source the_source) {
      this.statements = statements;
      this.the_source = the_source;
    }
    public List<construct> statements() {
      return statements;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("block_construct", START_OBJECT, NEWLINE, indent(field_is("statements", statements), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class conditional_construct implements construct {
    private final construct conditional;
    private final construct then_branch;
    private final @Nullable construct else_branch;
    private final source the_source;
    public conditional_construct(construct conditional, construct then_branch, @Nullable construct else_branch, source the_source) {
      this.conditional = conditional;
      this.then_branch = then_branch;
      this.else_branch = else_branch;
      this.the_source = the_source;
    }
    public construct conditional() {
      return conditional;
    }
    public construct then_branch() {
      return then_branch;
    }
    public @Nullable construct else_branch() {
      return else_branch;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("conditional_construct", START_OBJECT, NEWLINE, indent(field_is("conditional", conditional), field_is("then_branch", then_branch), field_is("else_branch", else_branch), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class return_construct implements construct {
    private final @Nullable construct expression;
    private final source the_source;
    public return_construct(@Nullable construct expression, source the_source) {
      this.expression = expression;
      this.the_source = the_source;
    }
    public @Nullable construct expression() {
      return expression;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("return_construct", START_OBJECT, NEWLINE, indent(field_is("expression", expression), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class variable_construct implements construct {
    private final List<modifier_construct> modifiers;
    private final @Nullable construct type;
    private final String name;
    private final @Nullable construct initializer;
    private final source the_source;
    public variable_construct(List<modifier_construct> modifiers, @Nullable construct type, String name, @Nullable construct initializer, source the_source) {
      this.modifiers = modifiers;
      this.type = type;
      this.name = name;
      this.initializer = initializer;
      this.the_source = the_source;
    }
    public List<modifier_construct> modifiers() {
      return modifiers;
    }
    public @Nullable construct type() {
      return type;
    }
    public String name() {
      return name;
    }
    public @Nullable construct initializer() {
      return initializer;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("variable_construct", START_OBJECT, NEWLINE, indent(field_is("modifiers", modifiers), field_is("type", type), field_is("name", name), field_is("initializer", initializer), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class procedure_construct implements construct {
    private final List<modifier_construct> modifiers;
    private final @Nullable construct return_type;
    private final String name;
    private final List<variable_construct> parameters;
    private final @Nullable construct body;
    private final source the_source;
    public procedure_construct(List<modifier_construct> modifiers, @Nullable construct return_type, String name, List<variable_construct> parameters, @Nullable construct body, source the_source) {
      this.modifiers = modifiers;
      this.return_type = return_type;
      this.name = name;
      this.parameters = parameters;
      this.body = body;
      this.the_source = the_source;
    }
    public List<modifier_construct> modifiers() {
      return modifiers;
    }
    public @Nullable construct return_type() {
      return return_type;
    }
    public String name() {
      return name;
    }
    public List<variable_construct> parameters() {
      return parameters;
    }
    public @Nullable construct body() {
      return body;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("procedure_construct", START_OBJECT, NEWLINE, indent(field_is("modifiers", modifiers), field_is("return_type", return_type), field_is("name", name), field_is("parameters", parameters), field_is("body", body), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class dispatch_construct implements construct {
    private final String name;
    private final construct the_type;
    private final source the_source;
    public dispatch_construct(String name, construct the_type, source the_source) {
      this.name = name;
      this.the_type = the_type;
      this.the_source = the_source;
    }
    public String name() {
      return name;
    }
    public construct the_type() {
      return the_type;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("dispatch_construct", START_OBJECT, NEWLINE, indent(field_is("name", name), field_is("the_type", the_type), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  enum supertype_kind {
    EXTENDS,
    IMPLEMENTS;
  }
  class supertype_construct implements construct {
    private final supertype_kind the_supertype_kind;
    private final List<construct> supertypes;
    private final source the_source;
    public supertype_construct(supertype_kind the_supertype_kind, List<construct> supertypes, source the_source) {
      this.the_supertype_kind = the_supertype_kind;
      this.supertypes = supertypes;
      this.the_source = the_source;
    }
    public supertype_kind the_supertype_kind() {
      return the_supertype_kind;
    }
    public List<construct> supertypes() {
      return supertypes;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("supertype_construct", START_OBJECT, NEWLINE, indent(field_is("the_supertype_kind", the_supertype_kind), field_is("supertypes", supertypes), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  enum type_kind {
    NAMESPACE,
    BLOCK,
    INTERFACE,
    DATATYPE,
    ENUM,
    CLASS,
    SINGLETON;
  }
  class type_construct implements construct {
    private final List<modifier_construct> modifiers;
    private final type_kind the_type_kind;
    private final String name;
    private final @Nullable List<construct> parameters;
    private final List<construct> body;
    private final source the_source;
    public type_construct(List<modifier_construct> modifiers, type_kind the_type_kind, String name, @Nullable List<construct> parameters, List<construct> body, source the_source) {
      this.modifiers = modifiers;
      this.the_type_kind = the_type_kind;
      this.name = name;
      this.parameters = parameters;
      this.body = body;
      this.the_source = the_source;
    }
    public List<modifier_construct> modifiers() {
      return modifiers;
    }
    public type_kind the_type_kind() {
      return the_type_kind;
    }
    public String name() {
      return name;
    }
    public @Nullable List<construct> parameters() {
      return parameters;
    }
    public List<construct> body() {
      return body;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("type_construct", START_OBJECT, NEWLINE, indent(field_is("modifiers", modifiers), field_is("the_type_kind", the_type_kind), field_is("name", name), field_is("parameters", parameters), field_is("body", body), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  abstract class construct_dispatch<result> implements function<result, construct> {
    @Override public result call(construct the_construct) {
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
      if (the_construct instanceof conditional_construct) {
        return call_conditional_construct((conditional_construct) the_construct);
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
      if (the_construct instanceof dispatch_construct) {
        return call_dispatch_construct((dispatch_construct) the_construct);
      }
      if (the_construct instanceof supertype_construct) {
        return call_supertype_construct((supertype_construct) the_construct);
      }
      if (the_construct instanceof type_construct) {
        return call_type_construct((type_construct) the_construct);
      }
      return call_construct(the_construct);
    }
    public abstract result call_construct(construct the_construct);
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
    public result call_conditional_construct(conditional_construct the_conditional_construct) {
      return call_construct(the_conditional_construct);
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
    public result call_dispatch_construct(dispatch_construct the_dispatch_construct) {
      return call_construct(the_dispatch_construct);
    }
    public result call_supertype_construct(supertype_construct the_supertype_construct) {
      return call_construct(the_supertype_construct);
    }
    public result call_type_construct(type_construct the_type_construct) {
      return call_construct(the_type_construct);
    }
  }
  interface type extends describable {
    String name();
  }
  interface analysis_result extends source {
  }
  interface action extends analysis_result {
    type result();
  }
  interface type_action extends action {
    type the_type();
    source the_source();
    type result();
  }
  class type_action_class implements type_action {
    private final type the_type;
    private final source the_source;
    public type_action_class(type the_type, source the_source) {
      this.the_type = the_type;
      this.the_source = the_source;
    }
    @Override public type the_type() {
      return the_type;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public type result() {
      return the_type;
    }
    @Override public text description() {
      return join_fragments("type_action_class", START_OBJECT, NEWLINE, indent(field_is("the_type", the_type), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  interface value_action extends action {
    type result();
    source the_source();
  }
  class value_action_class implements value_action {
    private final type result;
    private final source the_source;
    public value_action_class(type result, source the_source) {
      this.result = result;
      this.the_source = the_source;
    }
    @Override public type result() {
      return result;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("value_action_class", START_OBJECT, NEWLINE, indent(field_is("result", result), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  interface notification_message {
    notification_type type();
    String text();
  }
  class notification_message_class implements notification_message {
    private final notification_type type;
    private final String text;
    public notification_message_class(notification_type type, String text) {
      this.type = type;
      this.text = text;
    }
    @Override public notification_type type() {
      return type;
    }
    @Override public String text() {
      return text;
    }
  }
  class enum_literal implements value_action {
    private final String name;
    private final int ordinal;
    private final principal_type result;
    private final source the_source;
    public enum_literal(String name, int ordinal, principal_type result, source the_source) {
      this.name = name;
      this.ordinal = ordinal;
      this.result = result;
      this.the_source = the_source;
    }
    public String name() {
      return name;
    }
    public int ordinal() {
      return ordinal;
    }
    @Override public principal_type result() {
      return result;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("enum_literal", START_OBJECT, NEWLINE, indent(field_is("name", name), field_is("ordinal", ordinal), field_is("result", result), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class singleton_literal implements value_action {
    private final principal_type result;
    private final source the_source;
    public singleton_literal(principal_type result, source the_source) {
      this.result = result;
      this.the_source = the_source;
    }
    @Override public principal_type result() {
      return result;
    }
    @Override public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("singleton_literal", START_OBJECT, NEWLINE, indent(field_is("result", result), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class error_signal implements action {
    private final notification_message message;
    private final source the_source;
    public error_signal(notification_message message, source the_source) {
      this.message = message;
      this.the_source = the_source;
    }
    public notification_message message() {
      return message;
    }
    public source the_source() {
      return the_source;
    }
    @Override public type result() {
      return core_type.ERROR;
    }
    @Override public text description() {
      return join_fragments("error_signal", START_OBJECT, NEWLINE, indent(field_is("message", message), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  interface principal_type extends type {
    String name();
    type_kind the_type_kind();
    @Nullable principal_type parent();
  }
  class principal_type_class implements principal_type {
    private final String name;
    private final type_kind the_type_kind;
    private final @Nullable principal_type parent;
    public principal_type_class(String name, type_kind the_type_kind, @Nullable principal_type parent) {
      this.name = name;
      this.the_type_kind = the_type_kind;
      this.parent = parent;
    }
    @Override public String name() {
      return name;
    }
    @Override public type_kind the_type_kind() {
      return the_type_kind;
    }
    @Override public @Nullable principal_type parent() {
      return parent;
    }
    @Override public text description() {
      return join_fragments("principal_type_class", START_OBJECT, NEWLINE, indent(field_is("name", name), field_is("the_type_kind", the_type_kind), field_is("parent", parent)), END_OBJECT);
    }
  }
  class top_type implements principal_type {
    public static final top_type instance = new top_type();
    @Override public String name() {
      return "<top>";
    }
    @Override public type_kind the_type_kind() {
      return type_kind.NAMESPACE;
    }
    @Override public @Nullable principal_type parent() {
      return null;
    }
    @Override public text description() {
      return new text_string("top_type");
    }
  }
  enum core_type implements principal_type {
    VOID,
    INTEGER,
    STRING,
    LIST,
    SET,
    NULL,
    NULLABLE,
    UNREACHABLE,
    ERROR;
    @Override public type_kind the_type_kind() {
      return type_kind.CLASS;
    }
    @Override public principal_type parent() {
      return top_type.instance;
    }
    @Override public text description() {
      return new text_string(name());
    }
  }
  class parametrized_type implements principal_type {
    private final principal_type main;
    private final List<type> parameters;
    public parametrized_type(principal_type main, List<type> parameters) {
      this.main = main;
      this.parameters = parameters;
    }
    public principal_type main() {
      return main;
    }
    public List<type> parameters() {
      return parameters;
    }
    @Override public String name() {
      return main.name();
    }
    @Override public type_kind the_type_kind() {
      return main.the_type_kind();
    }
    @Override public principal_type parent() {
      return main.parent();
    }
    @Override public text description() {
      return join_fragments("parametrized_type", START_OBJECT, NEWLINE, indent(field_is("main", main), field_is("parameters", parameters)), END_OBJECT);
    }
  }
  class type_declaration implements analysis_result {
    private final principal_type declared_type;
    private final type_kind the_type_kind;
    private final source the_source;
    public type_declaration(principal_type declared_type, type_kind the_type_kind, source the_source) {
      this.declared_type = declared_type;
      this.the_type_kind = the_type_kind;
      this.the_source = the_source;
    }
    public principal_type declared_type() {
      return declared_type;
    }
    public type_kind the_type_kind() {
      return the_type_kind;
    }
    public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("type_declaration", START_OBJECT, NEWLINE, indent(field_is("declared_type", declared_type), field_is("the_type_kind", the_type_kind), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  class variable_declaration implements analysis_result {
    private final type value_type;
    private final String name;
    private final principal_type declared_in_type;
    private final source the_source;
    public variable_declaration(type value_type, String name, principal_type declared_in_type, source the_source) {
      this.value_type = value_type;
      this.name = name;
      this.declared_in_type = declared_in_type;
      this.the_source = the_source;
    }
    public type value_type() {
      return value_type;
    }
    public String name() {
      return name;
    }
    public principal_type declared_in_type() {
      return declared_in_type;
    }
    public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("variable_declaration", START_OBJECT, NEWLINE, indent(field_is("value_type", value_type), field_is("name", name), field_is("declared_in_type", declared_in_type), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  interface variable_action extends action {
    variable_declaration the_declaration();
    source the_source();
    type result();
  }
  class variable_action_class implements variable_action {
    private final variable_declaration the_declaration;
    public variable_action_class(variable_declaration the_declaration) {
      this.the_declaration = the_declaration;
    }
    @Override public variable_declaration the_declaration() {
      return the_declaration;
    }
    @Override public source the_source() {
      return the_declaration;
    }
    @Override public type result() {
      return the_declaration.value_type();
    }
    @Override public text description() {
      return join_fragments("variable_action_class", START_OBJECT, SPACE, describe(the_declaration), SPACE, END_OBJECT);
    }
  }
  class procedure_declaration implements analysis_result {
    private final type return_type;
    private final String name;
    private final List<variable_declaration> parameters;
    private final principal_type declared_in_type;
    private final source the_source;
    public procedure_declaration(type return_type, String name, List<variable_declaration> parameters, principal_type declared_in_type, source the_source) {
      this.return_type = return_type;
      this.name = name;
      this.parameters = parameters;
      this.declared_in_type = declared_in_type;
      this.the_source = the_source;
    }
    public type return_type() {
      return return_type;
    }
    public String name() {
      return name;
    }
    public List<variable_declaration> parameters() {
      return parameters;
    }
    public principal_type declared_in_type() {
      return declared_in_type;
    }
    public source the_source() {
      return the_source;
    }
    @Override public text description() {
      return join_fragments("procedure_declaration", START_OBJECT, NEWLINE, indent(field_is("return_type", return_type), field_is("name", name), field_is("parameters", parameters), field_is("declared_in_type", declared_in_type), field_is("the_source", the_source)), END_OBJECT);
    }
  }
  enum analysis_pass {
    TYPE_PASS,
    MEMBER_PASS,
    BODY_PASS;
  }
  interface analysis_context {
    void add_action(type the_type, String name, action the_action);
    @Nullable action get_action(type the_type, String name);
    void add_supertype(type subtype, type supertype);
    Set<type> get_all_supertypes(type the_type);
    Set<type> get_direct_subtypes(type the_type);
    void add_binding(construct the_construct, analysis_result the_analysis_result);
    @Nullable analysis_result get_binding(construct the_construct);
  }
  enum notification_type implements notification_message {
    UNRECOGNIZED_CHARACTER("Unrecognized character"),
    EOF_IN_STRING_LITERAL("End of file in string literal"),
    NEWLINE_IN_STRING_LITERAL("Newline in string literal"),
    PARSE_ERROR("Parse error"),
    CLOSE_PAREN_NOT_FOUND("Close parenthesis not found"),
    MODIFIER_EXPECTED("Modifier expected"),
    VARIABLE_EXPECTED("Variable expected"),
    ANALYSIS_ERROR("Analysis error"),
    SYMBOL_LOOKUP_FAILED("Symbol lookup failed"),
    TYPE_EXPECTED("Type expected"),
    NOT_PARAMETRIZABLE("Not parametrizable"),
    WRONG_ARITY("Wrong arity"),
    IDENTIFIER_EXPECTED("Identifier expected");
    private final String text;
    notification_type(String text) {
      this.text = text;
    }
    public String text() {
      return text;
    }
    @Override public notification_type type() {
      return this;
    }
  }
}

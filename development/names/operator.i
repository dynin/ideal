-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.development.names.operator_type;

--- All operators used in the ideal system.
class operator {
  extends debuggable;
  implements action_name;

  final operator_type type;
  final token_type name;
  final simple_name alpha_name;

  overload private operator(operator_type type, token_type name, string alpha_name) {
    this.type = type;
    this.name = name;
    this.alpha_name = simple_name.make(alpha_name);
  }

  -- TODO: make assignment a subclass.
  overload private operator(operator_type type, operator primary, token_type name,
      string alpha_name) {
    this(type, name, alpha_name);
    assert type == ASSIGNMENT;
    -- This looks ugly.  TODO: add make_assignment_op(...)
    assert name.name == primary.name.name ++ "=";
    assert alpha_name.to_string == primary.alpha_name ++ "_assign";
  }

  simple_name symbol() => alpha_name;

  string to_string() {
    -- TODO: disambiguate type
    return name_utilities.in_brackets(this.type ++ " " ++ name);
  }

  -- names from: https://svn.boost.org/trac/boost/wiki/Guidelines/Naming/Operators
  -- http://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B

  static ASSIGN : operator.new(INFIX, punctuation.EQUALS, "assign");

  static MULTIPLY : operator.new(INFIX, punctuation.ASTERISK, "multiply");
  static DIVIDE : operator.new(INFIX, punctuation.SLASH, "divide");
  static MODULO : operator.new(INFIX, punctuation.PERCENT, "modulo");
  static ADD : operator.new(INFIX, punctuation.PLUS, "add");
  static SUBTRACT : operator.new(INFIX, punctuation.MINUS, "subtract");
  static NEGATE : operator.new(PREFIX, punctuation.MINUS, "negate");

  static PRE_INCREMENT : operator.new(PREFIX, punctuation.PLUS_PLUS, "pre_increment");
  static CONCATENATE : operator.new(INFIX, punctuation.PLUS_PLUS, "concatenate");

  static EQUAL_TO : operator.new(INFIX, punctuation.EQUALS_EQUALS, "equal_to");
  static NOT_EQUAL_TO : operator.new(INFIX, punctuation.EXCLAMATION_MARK_EQUALS, "not_equal_to");
  static LESS : operator.new(INFIX, punctuation.LESS_THAN, "less");
  static GREATER : operator.new(INFIX, punctuation.GREATER_THAN, "greater");
  static LESS_EQUAL : operator.new(INFIX, punctuation.LESS_THAN_EQUALS, "less_equal");
  static GREATER_EQUAL : operator.new(INFIX, punctuation.GREATER_THAN_EQUALS, "greater_equal");
  static BIT_AND : operator.new(INFIX, punctuation.AMPERSAND, "bit_and");
  static XOR : operator.new(INFIX, punctuation.CARET, "xor");
  static BIT_OR : operator.new(INFIX, punctuation.VERTICAL_BAR, "bit_or");
  static LOGICAL_AND : operator.new(INFIX, punctuation.AMPERSAND_AMPERSAND, "logical_and");
  static LOGICAL_OR : operator.new(INFIX, punctuation.VERTICAL_BAR_VERTICAL_BAR, "logical_or");
  static LOGICAL_NOT : operator.new(PREFIX, punctuation.EXCLAMATION_MARK, "logical_not");
  static GENERAL_OR : operator.new(INFIX, keyword.OR, "general_or");

  static ADD_ASSIGN : operator.new(ASSIGNMENT, ADD, punctuation.PLUS_EQUALS, "add_assign");
  static SUBTRACT_ASSIGN : operator.new(ASSIGNMENT, SUBTRACT, punctuation.MINUS_EQUALS,
      "subtract_assign");
  static MULTIPLY_ASSIGN : operator.new(ASSIGNMENT, MULTIPLY, punctuation.ASTERISK_EQUALS,
      "multiply_assign");
  static CONCATENATE_ASSIGN : operator.new(ASSIGNMENT, CONCATENATE, punctuation.PLUS_PLUS_EQUALS,
      "concatenate_assign");

  static AS_OPERATOR : operator.new(INFIX, keyword.AS, "as_operator");
  static IS_OPERATOR : operator.new(INFIX, keyword.IS, "is_operator");
  static IS_NOT_OPERATOR : operator.new(INFIX, keyword.IS_NOT, "is_not_operator");

  static ALLOCATE : operator.new(PREFIX, keyword.NEW, "allocate");
}

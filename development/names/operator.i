-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.development.names.operator_type;
implicit import ideal.development.names.punctuation;
-- TODO: uncomment when implicit enum is introduced
--implicit import ideal.development.names.precedence;

--- All operators used in the ideal system.
class operator {
  extends debuggable;
  implements action_name;

  the operator_type;
  token_type name;
  simple_name alpha_name;
  the precedence;

  overload protected operator(the operator_type, token_type name, string alpha_name,
      the precedence) {
    this.the_operator_type = the_operator_type;
    this.name = name;
    this.alpha_name = simple_name.make(alpha_name);
    this.the_precedence = the_precedence;
  }

  simple_name symbol => alpha_name;

  string to_string() {
    return name_utilities.in_brackets(the_operator_type ++ " " ++ name);
  }

  -- names from: https://svn.boost.org/trac/boost/wiki/Guidelines/Naming/Operators
  -- http://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B

  static ASSIGN : operator.new(INFIX, EQUALS, "assign", ASSIGNMENT);

  static MULTIPLY : operator.new(INFIX, ASTERISK, "multiply", MULTIPLICATIVE);
  static DIVIDE : operator.new(INFIX, SLASH, "divide", MULTIPLICATIVE);
  static MODULO : operator.new(INFIX, PERCENT, "modulo", MULTIPLICATIVE);
  static ADD : operator.new(INFIX, PLUS, "add", ADDITIVE);
  static SUBTRACT : operator.new(INFIX, MINUS, "subtract", ADDITIVE);
  static NEGATE : operator.new(PREFIX, MINUS, "negate", ADDITIVE);

  static PRE_INCREMENT : operator.new(PREFIX, PLUS_PLUS, "pre_increment", UNARY);
  static CONCATENATE : operator.new(INFIX, PLUS_PLUS, "concatenate", precedence.CONCATENATE);

  static EQUAL_TO : operator.new(INFIX, EQUALS_EQUALS, "equal_to", EQUALITY);
  static NOT_EQUAL_TO : operator.new(INFIX, EXCLAMATION_MARK_EQUALS, "not_equal_to", EQUALITY);
  static LESS : operator.new(INFIX, LESS_THAN, "less", RELATIONAL);
  static GREATER : operator.new(INFIX, GREATER_THAN, "greater", RELATIONAL);
  static LESS_EQUAL : operator.new(INFIX, LESS_THAN_EQUALS, "less_equal", RELATIONAL);
  static GREATER_EQUAL : operator.new(INFIX, GREATER_THAN_EQUALS, "greater_equal", RELATIONAL);
  static COMPARE : operator.new(INFIX, LESS_THAN_EQUALS_GREATER_THAN, "compare", RELATIONAL);
  static BITWISE_AND : operator.new(INFIX, AMPERSAND, "bitwise_and", precedence.BITWISE_AND);
  static BITWISE_XOR : operator.new(INFIX, CARET, "bitwise_xor", precedence.BITWISE_XOR);
  static BITWISE_OR : operator.new(INFIX, VERTICAL_BAR, "bitwise_or", precedence.BITWISE_OR);
  static LOGICAL_AND : operator.new(INFIX, AMPERSAND_AMPERSAND, "logical_and",
      precedence.LOGICAL_AND);
  static LOGICAL_OR : operator.new(INFIX, VERTICAL_BAR_VERTICAL_BAR, "logical_or",
      precedence.LOGICAL_OR);
  static LOGICAL_NOT : operator.new(PREFIX, EXCLAMATION_MARK, "logical_not", UNARY);
  static GENERAL_OR : operator.new(INFIX, keywords.OR, "general_or", precedence.LOGICAL_OR);

  static ADD_ASSIGN : operator.new(INFIX, PLUS_EQUALS, "add_assign", ASSIGNMENT);
  static SUBTRACT_ASSIGN : operator.new(INFIX, MINUS_EQUALS, "subtract_assign", ASSIGNMENT);
  static MULTIPLY_ASSIGN : operator.new(INFIX, ASTERISK_EQUALS, "multiply_assign", ASSIGNMENT);
  static CONCATENATE_ASSIGN : operator.new(INFIX, PLUS_PLUS_EQUALS,
      "concatenate_assign", ASSIGNMENT);

  static SOFT_CAST : cast_type.new(DOT_GREATER_THAN, "soft_cast");
  static HARD_CAST : cast_type.new(EXCLAMATION_GREATER_THAN, "hard_cast");

  static IS_OPERATOR : operator.new(INFIX, keywords.IS, "is_operator", RELATIONAL);
  static IS_NOT_OPERATOR : operator.new(INFIX, keywords.IS_NOT, "is_not_operator", RELATIONAL);

  static ALLOCATE : operator.new(PREFIX, keywords.NEW, "allocate", UNARY);
}

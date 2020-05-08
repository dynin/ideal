/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import static ideal.development.names.operator_type.*;

public class operator extends debuggable implements action_name {

  public final operator_type type;
  public final token_type name;
  public final simple_name alpha_name;

  private operator(operator_type type, token_type name, String alpha_name) {
    this.type = type;
    this.name = name;
    this.alpha_name = simple_name.make(alpha_name);
  }

  // TODO: make assignment operator a subclass.
  private operator(operator_type type, operator primary, token_type name, String alpha_name) {
    this(type, name, alpha_name);
    assert type == ASSIGNMENT;
    // This looks ugly.  TODO: add make_assignment_op(...)
    assert utilities.eq(name.name(), new base_string(primary.name.name(), "="));
    assert utilities.eq(this.alpha_name.to_string(),
        new base_string(primary.alpha_name.to_string(), "_assign"));
  }

  public simple_name symbol() {
    return alpha_name;
  }

  public string to_string() {
    return name_utilities.in_brackets(new base_string(type.to_string(), " ", name.to_string()));
  }

  // operator names from: https://svn.boost.org/trac/boost/wiki/Guidelines/Naming/Operators
  // http://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B

  public static operator ASSIGN = new operator(INFIX, punctuation.EQUALS, "assign");

  public static operator MULTIPLY = new operator(INFIX, punctuation.ASTERISK, "multiply");
  public static operator DIVIDE = new operator(INFIX, punctuation.SLASH, "divide");
  public static operator MODULO = new operator(INFIX, punctuation.PERCENT, "modulo");
  public static operator ADD = new operator(INFIX, punctuation.PLUS, "add");
  public static operator SUBTRACT = new operator(INFIX, punctuation.MINUS, "subtract");
  public static operator NEGATE = new operator(PREFIX, punctuation.MINUS, "negate");

  // TODO: do we use preincrement?
  public static operator PRE_INCREMENT = new operator(PREFIX, punctuation.PLUS_PLUS,
      "pre_increment");
  public static operator CONCATENATE = new operator(INFIX, punctuation.PLUS_PLUS, "concatenate");

  public static operator EQUAL_TO = new operator(INFIX, punctuation.EQUALS_EQUALS, "equal_to");
  public static operator NOT_EQUAL_TO = new operator(INFIX, punctuation.EXCLAMATION_MARK_EQUALS,
      "not_equal_to");
  public static operator LESS = new operator(INFIX, punctuation.LESS_THAN, "less");
  public static operator GREATER = new operator(INFIX, punctuation.GREATER_THAN, "greater");
  public static operator LESS_EQUAL = new operator(INFIX, punctuation.LESS_THAN_EQUALS,
      "less_equal");
  public static operator GREATER_EQUAL = new operator(INFIX, punctuation.GREATER_THAN_EQUALS,
      "greater_equal");
  public static operator BIT_AND = new operator(INFIX, punctuation.AMPERSAND, "bit_and");
  public static operator XOR = new operator(INFIX, punctuation.CARET, "xor");
  public static operator BIT_OR = new operator(INFIX, punctuation.VERTICAL_BAR, "bit_or");
  public static operator LOGICAL_AND = new operator(INFIX, punctuation.AMPERSAND_AMPERSAND,
      "logical_and");
  public static operator LOGICAL_OR = new operator(INFIX, punctuation.VERTICAL_BAR_VERTICAL_BAR,
      "logical_or");
  public static operator LOGICAL_NOT = new operator(PREFIX, punctuation.EXCLAMATION_MARK,
      "logical_not");
  public static operator GENERAL_OR = new operator(INFIX, keyword.OR, "general_or");

  public static operator ADD_ASSIGN = new operator(ASSIGNMENT, ADD,
      punctuation.PLUS_EQUALS, "add_assign");
  public static operator SUBTRACT_ASSIGN = new operator(ASSIGNMENT, SUBTRACT,
      punctuation.MINUS_EQUALS, "subtract_assign");
  public static operator MULTIPLY_ASSIGN = new operator(ASSIGNMENT, MULTIPLY,
      punctuation.ASTERISK_EQUALS, "multiply_assign");
  public static operator CONCATENATE_ASSIGN = new operator(ASSIGNMENT, CONCATENATE,
      punctuation.PLUS_PLUS_EQUALS, "concatenate_assign");

  public static operator AS_OPERATOR = new operator(INFIX, keyword.AS, "as_operator");
  public static operator IS_OPERATOR = new operator(INFIX, keyword.IS, "is_operator");
  public static operator IS_NOT_OPERATOR = new operator(INFIX, keyword.IS_NOT, "is_not_operator");

  public static operator ALLOCATE = new operator(PREFIX, keyword.NEW, "allocate");
}

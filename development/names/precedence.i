-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Precedences of operators used in the ideal type system.
enum precedence {
  implements deeply_immutable data, reference_equality;
  implements stringable, displayable;

  POSTFIX;
  UNARY;
  MULTIPLICATIVE;
  ADDITIVE;
  CONCATENATE;
  SHIFT;
  RELATIONAL;
  EQUALITY;
  BITWISE_AND;
  BITWISE_XOR;
  BITWISE_OR;
  LOGICAL_AND;
  LOGICAL_OR;
  TERNARY;
  ASSIGNMENT;

  override string display => to_string;
}
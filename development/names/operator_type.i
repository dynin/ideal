-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Type of operator (prefix, postfix, infix).
--- TODO: convert to enum.
class operator_type {
  implements identifier, readonly displayable;
  extends debuggable;

  final string name;
  final nonnegative arity;

  private operator_type(string name, nonnegative arity) {
    this.name = name;
    this.arity = arity;
  }

  override string to_string => name;

  override string display() => to_string();

  static operator_type PREFIX : operator_type.new("prefix", 1);

  static operator_type POSTFIX : operator_type.new("postfix", 1);

  static operator_type INFIX : operator_type.new("infix", 2);
}

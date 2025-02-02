-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.development.names.operator;

meta_construct class operator_construct {
  operator the_operator;
  readonly list[construct] arguments;

  overload operator_construct(operator the_operator, readonly list[construct] arguments,
      origin the_origin) {
    super(the_origin);
    assert arguments.size == the_operator.the_operator_type.arity;
    this.the_operator = the_operator;
    this.arguments = arguments;
  }

  overload operator_construct(operator the_operator, construct argument, origin the_origin) {
    this(the_operator, [argument, ], the_origin);
  }

  overload operator_construct(operator the_operator, construct left, construct right,
      origin the_origin) {
    this(the_operator, [left, right], the_origin);
  }
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declarations of builtin operators.
package operators {
  implicit import ideal.library.elements;

  -- TODO: use writable reference.
  any value assign(any entity lvalue, any value rvalue);

  -- NOTE: fix types.
  any value soft_cast(any value the_value, entity the_type) pure;
  any value hard_cast(any value the_value, entity the_type) pure;

  -- NOTE: fix types.
  any value allocate(any value type);

  integer multiply(integer first, integer second) pure;
  integer add(integer first, integer second) pure;
  integer subtract(integer first, integer second) pure;
  integer modulo(integer first, integer second) pure;
  integer negate(integer argument) pure;

  -- TODO: handle equality_comparable as a first and both arguments.
  boolean equal_to(readonly value first, readonly equality_comparable second) pure;
  boolean not_equal_to(readonly value first, readonly equality_comparable second) pure;

  -- TODO: less operator should take arguments of type comparable,
  -- as defined below.
  -- boolean less(readonly comparable first, readonly comparable second) pure;
  boolean less(integer first, integer second) pure;
  boolean greater(readonly comparable first, readonly comparable second) pure;
  boolean less_equal(readonly comparable first, readonly comparable second) pure;
  boolean greater_equal(readonly comparable first, readonly comparable second) pure;
  sign compare(readonly comparable first, readonly comparable second) pure;

  boolean is_operator(entity argument, entity the_type) pure;
  boolean is_not_operator(entity argument, entity the_type) pure;

  boolean logical_and(boolean first, boolean second) pure;
  boolean logical_or(boolean first, boolean second) pure;
  boolean logical_not(boolean argument) pure;

  string concatenate(readonly stringable first,
      readonly stringable second) pure;

  overload integer add_assign(reference[integer] lvalue, integer rvalue);
  overload nonnegative add_assign(reference[nonnegative] lvalue, nonnegative rvalue);

  integer subtract_assign(reference[integer] lvalue, integer rvalue);
  integer multiply_assign(reference[integer] lvalue, integer rvalue);
  string concatenate_assign(reference[string] lvalue, string tail);

  -- TODO: range operator: a..b
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

package operators {
  implicit import ideal.library.elements;

  any value assign(any value lvalue mutable, any value rvalue);

  -- NOTE: fix types.
  any value as_operator(any value the_value, entity the_type) pure;

  -- NOTE: fix types.
  any value allocate(any value type);

  integer multiply(integer first, integer second) pure;
  integer add(integer first, integer second) pure;
  integer subtract(integer first, integer second) pure;
  integer modulo(integer first, integer second) pure;
  integer negate(integer argument) pure;

  boolean equal_to(readonly equality_comparable first, readonly equality_comparable second) pure;
  boolean not_equal_to(readonly equality_comparable first,
      readonly equality_comparable second) pure;

  -- TODO: less operator should take arguments of type comparable,
  -- as defined below.
  -- boolean less(readonly comparable first, readonly comparable second) pure;
  boolean less(integer first, integer second) pure;
  boolean greater(readonly comparable first, readonly comparable second) pure;
  boolean less_equal(readonly comparable first, readonly comparable second) pure;
  boolean greater_equal(readonly comparable first, readonly comparable second) pure;
  -- Comparison operator: <=>
  sign compare(readonly comparable first, readonly comparable second) pure;

  boolean is_operator(entity argument, entity the_type) pure;
  boolean is_not_operator(entity argument, entity the_type) pure;

  boolean logical_and(boolean first, boolean second) pure;
  boolean logical_or(boolean first, boolean second) pure;
  boolean logical_not(boolean argument) pure;

  string concatenate(readonly stringable first,
      readonly stringable second) pure;

  overload integer add_assign(integer lvalue mutable, integer rvalue);
  overload nonnegative add_assign(nonnegative lvalue mutable, nonnegative rvalue);

  integer subtract_assign(integer lvalue mutable, integer rvalue);
  integer multiply_assign(integer lvalue mutable, integer rvalue);
  string concatenate_assign(string lvalue mutable, string tail);

  -- TODO: range operator: a..b
}

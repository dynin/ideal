-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.reflections;

--- A type is a set of invariants that are preserved throughout entity's lifetime,
--- optionally including an algorithm for creating such entities.
interface type {
  extends abstract_value, data, stringable, reference_equality;
  subtypes type_id;

  boolean is_subtype_of(type the_supertype) pure;
  principal_type principal;
  type_flavor get_flavor;
  type get_flavored(type_flavor flavored) pure;
}

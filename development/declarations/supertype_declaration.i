-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declaration of a supertype for an enclosed type.
interface supertype_declaration {
  extends declaration;

  annotation_set annotations;
  type_flavor subtype_flavor;
  subtype_tag tag;
  readonly list[type] super_types;
  supertype_declaration specialize(specialization_context context, principal_type new_parent);
}

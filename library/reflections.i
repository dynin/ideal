-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declarations for interfaces used in reflection.
package reflections {
  implicit import ideal.library.elements;

  interface type_id {
    subtypes stringable, equality_comparable;

    identifier short_name;
  }

  --- Entity_wrapper is one of the three: value_wrapper, reference_wrapper, or jump_wrapper.
  interface entity_wrapper {
    extends stringable;

    type_id type_bound;
  }

  interface value_wrapper {
    extends entity_wrapper;

    any value unwrap;
  }

  interface reference_wrapper {
    extends entity_wrapper;

    type_id value_type_bound;

    value_wrapper get pure;
    init(value_wrapper the_value) writeonly;
    set(value_wrapper the_value) writeonly;
  }

  interface variable_id {
    subtypes stringable, equality_comparable;

    identifier short_name;
    type_id value_type;
    type_id reference_type;
  }

  --- Describes the context for accessing variables.
  --- Can refer to local stack, static frame, or composite object state.
  interface variable_context {
    put_var(variable_id key, value_wrapper value) writeonly;
    value_wrapper get_var(variable_id key) readonly;
  }
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declarations for serialization formats, currently JSON.
package formats {
  implicit import ideal.library.elements;

  interface json_data {
    extends data, equality_comparable;
  }

  supertype_of[integer] interface json_number {
    extends deeply_immutable json_data;
  }

  supertype_of[string] interface json_string {
    extends deeply_immutable json_data;
  }

  supertype_of[boolean] interface json_boolean {
    extends deeply_immutable json_data;
  }

  supertype_of[null] interface json_null {
    extends deeply_immutable json_data;
  }

  interface json_array {
    extends json_data;
    extends list[readonly json_data];
  }

  interface json_object {
    extends json_data;
    extends dictionary[string, readonly json_data];
  }
}

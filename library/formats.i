-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Declarations for serialization formats, currently JSON.
package formats {
  implicit import ideal.library.elements;

  interface json_data {
    extends data;
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
    extends list[readonly json_data];
  }

  interface json_object {
    extends dictionary[string, readonly json_data];
  }
}

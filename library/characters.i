-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Character-related functions.
package characters {
  implicit import ideal.library.elements;

  interface character_handler {
    extends deeply_immutable data;

    boolean is_upper_case(character c);
    character to_lower_case(character c);
  }
}

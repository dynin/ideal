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

    boolean is_letter(character the_character) pure;
    boolean is_letter_or_digit(character the_character) pure;
    boolean is_whitespace(character the_character) pure;
    boolean is_upper_case(character the_character) pure;

    character to_lower_case(character the_character);
  }
}

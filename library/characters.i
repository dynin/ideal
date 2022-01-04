-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Character-related functions, such as |is_letter()| or |is_whitespace()|.
package characters {
  implicit import ideal.library.elements;

  interface character_handler {
    extends deeply_immutable data;

    boolean is_letter(the character) pure;
    boolean is_letter_or_digit(the character) pure;
    boolean is_whitespace(the character) pure;
    boolean is_upper_case(the character) pure;
    boolean is_digit(the character) pure;

    nonnegative or null from_digit(the character, nonnegative radix) pure;

    character to_lower_case(the character) pure;
    character to_upper_case(the character) pure;

    string to_lower_case_all(the string) pure;
    string to_upper_case_all(the string) pure;

    nonnegative to_code(the character) pure;
    character from_code(nonnegative code) pure;
  }

  namespace radixes {
    MINIMUM_RADIX : 2;
    --- Most humans have ten fingers.
    DEFAULT_RADIX : 10;
    MAXIMUM_RADIX : 36;
  }
}

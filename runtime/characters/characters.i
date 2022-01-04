-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of the character-related functions,
--- such as |is_letter()| or |is_whitespace()|.
namespace characters {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.runtime.elements;

  class quoted_character;
  test_suite test_character_handler;
}

-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class quote_type {
  extends punctuation_type;

  character quote_character;

  quote_type(the character, string the_symbol_identifier) {
    super(utilities.string_of(the_character), the_symbol_identifier);
    quote_character = the_character;
  }
}

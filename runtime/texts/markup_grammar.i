-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- The grammar for a subset of XML.
class markup_grammar {
  character_handler the_character_handler;

  markup_grammar(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
  }

  boolean is_whitespace(character the_character) {
    return the_character_handler.is_whitespace(the_character);
  }

  boolean is_letter(character the_character) {
    return the_character_handler.is_letter(the_character);
  }

  string parse(string input) {
    var nonnegative index : 0;

    while(index < input.size && is_whitespace(input[index])) {
      index += 1;
    }
    begin : index;

    while(index < input.size && is_letter(input[index])) {
      index += 1;
    }
    end : index;

    return input.slice(begin, end);
  }
}

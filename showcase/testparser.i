-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.elements;
implicit import ideal.library.characters;
implicit import ideal.library.texts;
implicit import ideal.runtime.elements;

--- The grammar for a subset of XML.
class testparser {
  character_handler the_character_handler;

  testparser(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
  }

--  boolean is_whitespace(character the_character) {
--    return the_character_handler.is_whitespace(the_character);
--  }

  boolean is_letter(character the_character) {
    return the_character_handler.is_letter(the_character);
  }

  string parse(string input) {
    function[boolean, character] is_whitespace : the_character_handler.is_whitespace;

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

  nonnegative or null match_prefix(readonly list[character] the_list) {
    return missing.instance;
  }
}

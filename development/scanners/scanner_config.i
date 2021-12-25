-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

interface scanner_config {
  character_handler the_character_handler;
  boolean is_whitespace(the character) pure;
  boolean is_name_start(the character) pure;
  boolean is_name_part(the character) pure;
  readonly list[scanner_element] elements;

  token[deeply_immutable data] process_token(the token[deeply_immutable data]) pure;

  add_keyword(the keyword);
  add_punctuation(the punctuation_type);
  add_special(the special_name, token_type the_token_type);
  add_kind(the kind);
  add_subtype_tag(the subtype_tag);
  add_modifier(the modifier_kind);
  add_flavor(the type_flavor);
  add_reserved(string reserved_word, keyword or null the_keyword);

  readonly list[token[deeply_immutable data]] scan(source_content source);
}

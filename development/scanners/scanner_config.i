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

  token process_token(the token) pure;

  void add_keyword(the keyword);
  void add_punctuation(the punctuation_type);
  void add_special(the special_name, token_type the_token_type);
  void add_kind(the kind);
  void add_subtype_tag(the subtype_tag);
  void add_modifier(the modifier_kind);
  void add_flavor(the type_flavor);
  void add_reserved(string reserved_word, keyword or null the_keyword);

  readonly list[token] scan(source_content source);
}
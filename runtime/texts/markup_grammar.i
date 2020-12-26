-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- The grammar for a subset of XML.
---
--- Used https://cs.lmu.edu/~ray/notes/xmlgrammar/ as a reference.
class markup_grammar {
  implicit import ideal.library.patterns;
  implicit import ideal.runtime.patterns;

  character_handler the_character_handler;
  pattern[character] document_pattern;

  markup_grammar(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    this.document_pattern = this.document();
  }

  protected boolean name_start(character c) pure {
    return the_character_handler.is_letter(c) || c == '_' || c == ':';
  }

  protected boolean name_char(character c) pure {
    return the_character_handler.is_letter(c) || c == '.' || c == '-' || c == '_' || c == ':';
  }

  protected boolean content_char(character c) pure {
    return c != '<' && c != '&';
  }

  protected pattern[character] one(function[boolean, character] the_predicate) pure {
    return predicate_pattern[character].new(the_predicate);
  }

  protected pattern[character] one_char(character the_character) pure {
    return singleton_pattern[character].new(the_character);
  }

  protected pattern[character] zero_or_more(function[boolean, character] the_predicate) pure {
    return repeat_pattern[character].new(the_predicate, true);
  }

  protected string as_string_procedure(readonly list[character] the_character_list) pure {
    return the_character_list.frozen_copy() as base_string;
  }

  protected matcher[character, string] as_string(pattern[character] the_pattern) pure {
    return procedure_matcher[character, string].new(the_pattern, as_string_procedure);
  }

  protected pattern[character] space_opt() pure {
    return zero_or_more(the_character_handler.is_whitespace);
  }

  protected matcher[character, string] name() pure {
    return as_string(sequence_pattern[character].new([ one(name_start), zero_or_more(name_char) ]));
  }

  protected string select_2nd(readonly list[any value] the_list) pure => the_list[1] as string;

  protected pattern[character] lt() pure {
    return one_char('<');
  }

  protected pattern[character] gt() pure {
    return one_char('>');
  }

  protected pattern[character] slash() pure {
    return one_char('/');
  }

  protected pattern[character] start_tag() {
    return sequence_pattern[character].new([
        lt(), name(), space_opt(), gt()
    ]);
  }

  protected pattern[character] end_tag() {
    return sequence_pattern[character].new([
        lt(), slash(), name(), space_opt(), gt()
    ]);
  }

  protected pattern[character] char_data() pure {
    return zero_or_more(content_char);
  }

  protected pattern[character] content() {
    return char_data();
  }

  protected pattern[character] element() {
    return sequence_pattern[character].new([
        start_tag(), content(), end_tag()
    ]);
  }

  protected pattern[character] document() {
    -- sequence_matcher[character, string].new([ space_opt(), name(), space_opt() ], select_2nd);
    return sequence_pattern[character].new([ space_opt(), element(), space_opt() ]);
  }
}

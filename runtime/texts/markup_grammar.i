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

  protected pattern[character] one_character(character the_character) pure {
    return singleton_pattern[character].new(the_character);
  }

  protected pattern[character] zero_or_more(function[boolean, character] the_predicate) pure {
    return repeat_element[character].new(the_predicate, true);
  }

  protected pattern[character] repeat_or_none(pattern[character] the_pattern) pure {
    return repeat_pattern[character].new(the_pattern, true);
  }

  protected pattern[character] sequence(readonly list[pattern[character]] patterns_list) {
    return sequence_pattern[character].new(patterns_list);
  }

  protected string as_string_procedure(readonly list[character] the_character_list) pure {
    return the_character_list.frozen_copy() as base_string;
  }

  protected matcher[character, string] as_string(pattern[character] the_pattern) pure {
    return procedure_matcher[character, string].new(the_pattern, as_string_procedure);
  }

  protected string select_2nd(readonly list[any value] the_list) pure => the_list[1] as string;

  protected pattern[character] document() {
    space_opt : zero_or_more(the_character_handler.is_whitespace);
    name : as_string(sequence_pattern[character].new([ one(name_start), zero_or_more(name_char) ]));
    lt : one_character('<');
    gt : one_character('>');
    slash : one_character('/');

    char_data_opt : zero_or_more(content_char);
    empty_element : sequence([ lt, name, space_opt, slash, gt]);
    element : option_pattern[character].new([empty_element, ]);
    content : sequence([ char_data_opt, repeat_or_none(sequence([ element, char_data_opt ])) ]);

    start_tag : sequence([ lt, name, space_opt, gt ]);
    end_tag : sequence([ lt, slash, name, space_opt, gt ]);
    element.add_option(sequence([ start_tag, content, end_tag ]));

    -- sequence_matcher[character, string].new([ space_opt, element, space_opt ], select_2nd);
    return sequence([ space_opt, element, space_opt ]);
  }
}

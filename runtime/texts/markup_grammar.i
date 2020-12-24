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
  matcher[character, string] document_matcher;

  markup_grammar(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    this.document_matcher = this.document();
  }

  boolean name_start(character c) pure {
    return the_character_handler.is_letter(c) || c == '_' || c == ':';
  }

  boolean name_char(character c) pure {
    return the_character_handler.is_letter(c) || c == '.' || c == '-' || c == '_' || c == ':';
  }

  private pattern[character] one(function[boolean, character] the_predicate) pure {
    return predicate_pattern[character].new(the_predicate);
  }

  private pattern[character] zero_or_more(function[boolean, character] the_predicate) pure {
    return repeat_pattern[character].new(the_predicate, false);
  }

  private string as_string_procedure(readonly list[character] the_character_list) pure {
    return the_character_list.frozen_copy() as base_string;
  }

  private matcher[character, string] as_string(pattern[character] the_pattern) pure {
    return procedure_matcher[character, string].new(the_pattern, as_string_procedure);
  }

  private pattern[character] space_opt() pure {
    return zero_or_more(the_character_handler.is_whitespace);
  }

  private matcher[character, string] name() pure {
    return as_string(sequence_pattern[character].new([ one(name_start), zero_or_more(name_char) ]));
  }

  private string select_2nd(readonly list[any value] the_list) pure => the_list[1] as string;

  private matcher[character, string] document() {
    return sequence_matcher[character, string].new([ space_opt(), name(), space_opt() ],
        select_2nd);
  }
}

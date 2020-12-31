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
  implicit import character_patterns;

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

  protected boolean content_not_apos(character c) pure {
    return c != '<' && c != '&' && c != '\'';
  }

  protected boolean content_not_quot(character c) pure {
    return c != '<' && c != '&' && c != '"';
  }

  protected pattern[character] document() {
    lt : one_character('<');
    gt : one_character('>');
    slash : one_character('/');
    amp : one_character('&');
    semicolon : one_character(';');
    quot : one_character('"');
    apos : one_character('\'');
    eq : one_character('=');

    space_opt : zero_or_more(the_character_handler.is_whitespace);
    name : as_string(sequence_pattern[character].new([ one(name_start), zero_or_more(name_char) ]));

    entity_ref : sequence([ amp, name, semicolon ]);
    equals : sequence([ space_opt, eq, space_opt ]);
    attribute_value_in_quot : sequence([ quot,
        repeat_or_none(option([one_or_more(content_not_quot), entity_ref])), quot ]);
    attribute_value_in_apos : sequence([ apos,
        repeat_or_none(option([one_or_more(content_not_apos), entity_ref])), apos ]);
    attribute_value : option([ attribute_value_in_quot, attribute_value_in_apos ]);
    attribute : sequence([ name, equals, attribute_value ]);
    attributes : repeat_or_none(sequence([ space_opt, attribute ]));

    empty_element : sequence([ lt, name, attributes, space_opt, slash, gt ]);
    element : option([empty_element, ]);
    char_data_opt : zero_or_more(content_char);
    content_element : option([ element, entity_ref ]);
    content_tail : repeat_or_none(sequence([ content_element, char_data_opt ]));
    content : sequence([ char_data_opt, content_tail ]);

    start_tag : sequence([ lt, name, attributes, space_opt, gt ]);
    end_tag : sequence([ lt, slash, name, space_opt, gt ]);
    element.add_option(sequence([ start_tag, content, end_tag ]));

    -- sequence_matcher[character, string].new([ space_opt, element, space_opt ], select_2nd);
    return sequence([ space_opt, element, space_opt ]);
  }
}

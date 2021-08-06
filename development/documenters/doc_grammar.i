-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- The grammar for documentation comments.
class doc_grammar {
  extends markup_grammar;

  implicit import ideal.library.patterns;
  implicit import ideal.runtime.patterns;

  doc_grammar(character_handler the_character_handler) {
    super(the_character_handler);

    add_elements(text_library.HTML_ELEMENTS);
    add_attributes(text_library.HTML_ATTRIBUTES);
    add_entities(text_library.HTML_ENTITIES);
    add_elements(doc_elements.HTML_ELEMENTS);

    complete();
  }

  override boolean content_char(character c) pure {
    return c != '|' && super.content_char(c);
  }

  text_element match_vbar_element(readonly list[any value] the_list) pure {
    text_content : the_list[1] !> text_fragment;

    return base_element.new(doc_elements.CODE, text_content);
  }

  override void update_matchers() {
    vbar : one_character('|');

    element.add_option(sequence_matcher[character, text_element].new(
        [ vbar, content, vbar ], match_vbar_element));
  }

  text_fragment parse_content(string text, doc_parser parser) {
    this.parser = parser;
    return content.parse(text);
  }
}

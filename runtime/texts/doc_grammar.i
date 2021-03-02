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
  }

  override void update_matchers() {
  }

  text_fragment parse_content(string text, doc_parser parser) {
    this.parser = parser;
    return content.parse(text);
  }
}

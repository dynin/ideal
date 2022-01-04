-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- An object encapsulating documentation parser functionality.
class doc_parser {
  extends markup_parser;

  doc_parser(the doc_grammar, procedure[void, string] error_reporter) {
    super(the_doc_grammar, error_reporter);
  }

  text_fragment parse_content(string text) {
    return (the_markup_grammar !> doc_grammar).parse_content(text, this);
  }
}

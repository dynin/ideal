-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An object encapsulating documentation parser functionality.
class doc_parser {
  extends markup_parser;

  doc_parser(doc_grammar grammar, procedure[void, string] error_reporter) {
    super(grammar, error_reporter);
  }

  text_fragment parse_content(string text) {
    return (grammar !> doc_grammar).parse_content(text, this);
  }
}

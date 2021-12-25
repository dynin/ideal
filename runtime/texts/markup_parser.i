-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An object encapsulating markup parser functionality.
class markup_parser {
  the markup_grammar;
  procedure[void, string] error_reporter;

  markup_parser(the markup_grammar, procedure[void, string] error_reporter) {
    this.the_markup_grammar = the_markup_grammar;
    this.error_reporter = error_reporter;
  }

  -- TODO: pass along error position.
  report_error(string error_message) {
    error_reporter(error_message);
  }

  text_element parse(string text) {
    return the_markup_grammar.parse(text, this);
  }
}

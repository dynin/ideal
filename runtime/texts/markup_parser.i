-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An object encapsulating markup parser functionality.
class markup_parser {
  markup_grammar grammar;
  procedure[void, string] error_reporter;

  markup_parser(markup_grammar grammar, procedure[void, string] error_reporter) {
    this.grammar = grammar;
    this.error_reporter = error_reporter;
  }

  -- TODO: pass along error position.
  void report_error(string error_message) {
    error_reporter(error_message);
  }

  text_element parse(string text) {
    return grammar.parse(text, this);
  }
}

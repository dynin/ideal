-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class grammar_scanner {
  void register_tokens(the scanner_config) {
    the_scanner_config.add_punctuation(punctuation.COLON_COLON_EQUALS);

    the_scanner_config.add_keyword(keywords.GRAMMAR);
    the_scanner_config.add_keyword(keywords.TERMINAL);
    the_scanner_config.add_keyword(keywords.NONTERMINAL);
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_json_parser {
  import ideal.machine.characters.normal_handler;

  private json_parser make_parser() {
    return json_parser.new(normal_handler.instance);
  }

  test_case test_tokenizer() {
    parser : make_parser();

    words : parser.test_tokenize("  \"foo\"  \"bar\" ");
    assert words.size == 2;
    assert words[0] == "foo";
    assert words[1] == "bar";

    words2 : parser.test_tokenize("[ \"foo\"  \"bar\" ] ");
    assert words2.size == 4;
    assert words2[0] == json_token.OPEN_BRACKET;
    assert words2[1] == "foo";
    assert words2[2] == "bar";
    assert words2[3] == json_token.CLOSE_BRACKET;
  }
}

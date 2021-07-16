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

    words2 : parser.test_tokenize("[ \"foo\" , \"bar\" ] ");
    assert words2.size == 5;
    assert words2[0] == json_token.OPEN_BRACKET;
    assert words2[1] == "foo";
    assert words2[2] == json_token.COMMA;
    assert words2[3] == "bar";
    assert words2[4] == json_token.CLOSE_BRACKET;

    words3 : parser.test_tokenize("{ \"foo\" : \"bar\", \"baz\":68, \"x\":\"\\\"y\\\"\" } ");
    assert words3.size == 13;
    assert words3[0] == json_token.OPEN_BRACE;
    assert words3[1] == "foo";
    assert words3[2] == json_token.COLON;
    assert words3[3] == "bar";
    assert words3[4] == json_token.COMMA;
    assert words3[5] == "baz";
    assert words3[6] == json_token.COLON;
    -- TODO: we shouldn't need this (without this, generates invalid Java code.)
    w7 : words3[7];
    assert (w7 !> integer) == 68;
    assert words3[8] == json_token.COMMA;
    assert words3[9] == "x";
    assert words3[10] == json_token.COLON;
    assert words3[11] == "\"y\"";
    assert words3[12] == json_token.CLOSE_BRACE;
  }
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_json_parser {
  import ideal.machine.characters.unicode_handler;

  private json_parser make_parser() {
    return json_parser.new(unicode_handler.instance);
  }

  test_case test_tokenizer() {
    parser : make_parser();

    words : parser.test_tokenize("  \"foo\"  \"bar\" ");
    assert words.size == 2;
    assert words[0] == "foo";
    assert words[1] == "bar";

    words2 : parser.test_tokenize("[ \"foo\" , \"bar\", -68  ] ");
    assert words2.size == 7;
    assert words2[0] == json_token.OPEN_BRACKET;
    assert words2[1] == "foo";
    assert words2[2] == json_token.COMMA;
    assert words2[3] == "bar";
    assert words2[4] == json_token.COMMA;
    assert words2[5] == -68;
    assert words2[6] == json_token.CLOSE_BRACKET;

    words3 : parser.test_tokenize("{ \"foo\" : \"bar\", \"baz\":68, \"x\":\"\\\"y\\\"\" } ");
    assert words3.size == 13;
    assert words3[0] == json_token.OPEN_BRACE;
    assert words3[1] == "foo";
    assert words3[2] == json_token.COLON;
    assert words3[3] == "bar";
    assert words3[4] == json_token.COMMA;
    assert words3[5] == "baz";
    assert words3[6] == json_token.COLON;
    assert words3[7] == 68;
    assert words3[8] == json_token.COMMA;
    assert words3[9] == "x";
    assert words3[10] == json_token.COLON;
    assert words3[11] == "\"y\"";
    assert words3[12] == json_token.CLOSE_BRACE;

    words4 : parser.test_tokenize(" \"special: \\\\ \\u0066\\u006f\\u006f\" ");
    assert words4.size == 1;
    assert words4[0] == "special: \\ foo";

    words5 : parser.test_tokenize("[ true , false, null ] ");
    assert words5.size == 7;
    assert words5[0] == json_token.OPEN_BRACKET;
    -- TODO: we shouldn't need this (without this, generates invalid Java code.)
    w51 : words5[1];
    assert w51 !> boolean;
    assert words5[2] == json_token.COMMA;
    w53 : words5[3];
    assert !(w53 !> boolean);
    assert words5[4] == json_token.COMMA;
    w55 : words5[5];
    assert w55 is null;
    assert words5[6] == json_token.CLOSE_BRACKET;
  }

  test_case test_parser() {
    parser : make_parser();

    parsed0 : parser.parse(" \"foo\" ");
    assert parsed0 == "foo";

    parsed1 : parser.parse(" 68 ");
    assert parsed1 == 68;

    parsed2 : parser.parse(" false ");
    -- TODO: cast is redundant
    assert (parsed2 !> boolean) == false;

    parsed3 : parser.parse("{ \"foo\" : \"bar\", \"baz\":68 } ") !> readonly json_object;
    assert parsed3.size == 2;
    assert parsed3.get("foo") == "bar";
    assert parsed3.get("baz") == 68;

    parsed4 : parser.parse("[ \"foo\" , \"bar\", -68  ] ") !> readonly json_array;
    assert parsed4.size == 3;
    assert parsed4[0] == "foo";
    assert parsed4[1] == "bar";
    assert parsed4[2] == -68;

    parsed5 : parser.parse("{ \"foo\" : [ \"bar\", true ],\"baz\":-68 } ") !> readonly json_object;
    assert parsed5.size == 2;
    the_object : parsed5.get("foo") !> readonly json_array;
    assert the_object[0] == "bar";
    -- TODO: cast is redundant
    assert (the_object[1] !> boolean) == true;
    assert parsed5.get("baz") == -68;
  }
}

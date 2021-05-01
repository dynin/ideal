-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_sequence_matcher {

  private boolean match_a(character c) pure {
    return c == 'a' || c == 'A';
  }

  private boolean match_b(character c) pure {
    return c == 'b' || c == 'B';
  }

  private boolean match_c(character c) pure {
    return c == 'c' || c == 'C';
  }

  private string as_string(readonly list[character] char_list) {
    return char_list.frozen_copy() !> base_string;
  }

  matcher[character, string] make_matcher(function[boolean, character] the_predicate) {
    return procedure_matcher[character, string].new(
        repeat_element[character].new(the_predicate, false), as_string);
  }

  string match_procedure(readonly list[any value] the_list) {
    var string result : "";
    for (element : the_list) {
      assert element is string;
      result = result ++ "-" ++ element;
    }
    return result;
  }

  test_case test_match_parse() {
    -- TODO: use array creation expression.
    list[pattern[character]] patterns_list : base_list[pattern[character]].new(
        make_matcher(match_a),
        make_matcher(match_b),
        make_matcher(match_c)
    );

    the_matcher : sequence_matcher[character, string].new(patterns_list, match_procedure);

    assert the_matcher("abc");
    assert the_matcher("AbC");
    assert the_matcher("AaaBbCcc");
    assert the_matcher("AaaBBBCcc");
    assert !the_matcher("bac");
    assert !the_matcher("aabb");
    assert !the_matcher("aaca");

    assert the_matcher.parse("abc") == "-a-b-c";
    assert the_matcher.parse("AbC") == "-A-b-C";
    assert the_matcher.parse("AaaBbCcc") == "-Aaa-Bb-Ccc";
    assert the_matcher.parse("AaaBBBCcc") == "-Aaa-BBB-Ccc";
  }
}

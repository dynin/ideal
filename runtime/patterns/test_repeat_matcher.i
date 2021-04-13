-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_repeat_matcher {

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

  -- TODO: use list.join() when it's implemented.
  private string join_list(readonly list[string] strings) {
    var string result : "";
    for (the_string : strings) {
      if (result.is_empty) {
        result = the_string;
      } else {
        result = result ++ "/" ++ the_string;
      }
    }
    return result;
  }

  -- TODO: should be matcher[character, string]
  pattern[character] make_matcher(function[boolean, character] the_predicate) {
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

  matcher[character, string] make_pattern(boolean do_match_empty) {
    matcher_list : [ make_matcher(match_a), make_matcher(match_b), make_matcher(match_c) ];

    return repeat_matcher[character, string, string].new(
        sequence_matcher[character, string].new(matcher_list, match_procedure),
        do_match_empty,
        join_list);
  }

  testcase test_match() {
    the_matcher : make_pattern(true);

    assert the_matcher("abc");
    assert the_matcher("AbCabc");
    assert the_matcher("AaaBbCccABC");
    assert the_matcher("AaaBbCccABCabc");
    assert the_matcher("AaaBBBCcc");
    assert !the_matcher("bac");
    assert !the_matcher("aabb");
    assert !the_matcher("aaca");
  }

  testcase test_parse() {
    the_matcher : make_pattern(true);

    assert the_matcher.parse("abc") == "-a-b-c";
    assert the_matcher.parse("AbC") == "-A-b-C";
    assert the_matcher.parse("AaaBbCcc") == "-Aaa-Bb-Ccc";
    assert the_matcher.parse("AaaBBBCcc") == "-Aaa-BBB-Ccc";

    assert the_matcher.parse("abcABC") == "-a-b-c/-A-B-C";
    assert the_matcher.parse("AbCaBc") == "-A-b-C/-a-B-c";
    assert the_matcher.parse("AABBCCabcABC") == "-AA-BB-CC/-a-b-c/-A-B-C";
  }
}

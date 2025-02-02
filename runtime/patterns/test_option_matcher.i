-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_option_matcher {

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
    return "*" ++ (char_list.frozen_copy !> string);
  }

  matcher[character, string] make_matcher(function[boolean, character] the_predicate) {
    return procedure_matcher[character, string].new(
        repeat_element[character].new(the_predicate, false), as_string);
  }

  test_case test_match_parse() {
    matchers : [ make_matcher(match_a), make_matcher(match_b), make_matcher(match_c) ];
    the_matcher : option_matcher[character, string].new(matchers);
    the_matcher.validate();

    assert the_matcher("a");
    assert the_matcher("Bbb");
    assert the_matcher("Cccc");
    assert !the_matcher("abc");
    assert !the_matcher("aabb");
    assert !the_matcher("aaca");

    assert the_matcher.parse("aaa") == "*aaa";
    assert the_matcher.parse("Bbb") == "*Bbb";
    assert the_matcher.parse("CCCccc") == "*CCCccc";
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_procedure_matcher {

  private boolean test_predicate(character c) pure {
    return c == 'a' || c == 'b' || c == 'c';
  }

  private string as_string(readonly list[character] char_list) {
    return char_list.frozen_copy() as base_string;
  }

  matcher[character, string] make_matcher() {
    return procedure_matcher[character, string].new(
        repeat_element[character].new(test_predicate, false), as_string);
  }

  testcase test_match() {
    the_matcher : make_matcher();

    assert the_matcher("a");
    assert the_matcher("ab");
    assert the_matcher("abca");
    assert !the_matcher("");
    assert !the_matcher("abcda");
    assert !the_matcher("y");
    assert !the_matcher("xab");

    assert the_matcher.parse("a") == "a";
    assert the_matcher.parse("ab") == "ab";
    assert the_matcher.parse("abca") == "abca";
  }

  testcase test_viable_prefix() {
    the_pattern : make_matcher();

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("a");
    assert the_pattern.is_viable_prefix("ab");
    assert !the_pattern.is_viable_prefix("y");
    assert !the_pattern.is_viable_prefix("ay");
  }

  testcase test_match_prefix() {
    the_pattern : make_matcher();

    assert the_pattern.match_prefix("") is null;
    assert the_pattern.match_prefix("a") == 1;
    assert the_pattern.match_prefix("abc") == 3;
    assert the_pattern.match_prefix("abcdef") == 3;
    assert the_pattern.match_prefix("x") is null;
    assert the_pattern.match_prefix("xabc") is null;
    assert the_pattern.match_prefix("abcabc") == 6;
  }

  testcase test_find_first() {
    the_pattern : make_matcher();

    assert the_pattern.find_first("", 0) is null;
    assert the_pattern.find_first("foo", 0) is null;
    assert the_pattern.find_first("bfoo", 1) is null;

    match : the_pattern.find_first("a", 0);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_first("-abc-", 0);
    assert match2 is_not null;
    assert match2.begin == 1;
    assert match2.end == 4;

    match3 : the_pattern.find_first("ayzzybacy", 2);
    assert match3 is_not null;
    assert match3.begin == 5;
    assert match3.end == 8;
  }

  testcase test_split() {
    the_pattern : make_matcher();

    split0: the_pattern.split("foo");
    assert split0.size == 1;
    assert equals(split0[0], "foo");

    split1: the_pattern.split("fooabcxyzc");
    assert split1.size == 3;
    assert equals(split1[0], "foo");
    assert equals(split1[1], "xyz");
    assert equals(split1[2], "");

    split2: the_pattern.split("ab1bc2ca3");
    assert split2.size == 4;
    assert equals(split2[0], "");
    assert equals(split2[1], "1");
    assert equals(split2[2], "2");
    assert equals(split2[3], "3");
  }

  -- TODO: This hack shouldn't be needed.
  boolean equals(immutable list[character] s0, string s1) {
    -- deeply_immutable list[character] dil : s0;
    return (s0 as string) == s1;
  }
}

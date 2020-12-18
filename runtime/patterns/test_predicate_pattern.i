-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_predicate_pattern {

  private boolean test_predicate(character c) pure {
    return c == 'a' || c == 'b' || c == 'c';
  }

  testcase test_match() {
    the_pattern : predicate_pattern[character].new(test_predicate);

    assert the_pattern("a");
    assert !the_pattern("y");
    assert !the_pattern("xx");
  }

  testcase test_viable_prefix() {
    the_pattern : predicate_pattern[character].new(test_predicate);

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("a");
    assert !the_pattern.is_viable_prefix("y");
    assert !the_pattern.is_viable_prefix("aa");
  }

  testcase test_match_prefix() {
    the_pattern : predicate_pattern[character].new(test_predicate);

    assert the_pattern.match_prefix("") is null;
    assert the_pattern.match_prefix("a") == 1;
    assert the_pattern.match_prefix("x") is null;
    assert the_pattern.match_prefix("abcdef") == 1;
  }

  testcase test_find_first() {
    the_pattern : predicate_pattern[character].new(test_predicate);

    assert the_pattern.find_first("", 0) is null;
    assert the_pattern.find_first("foo", 0) is null;
    assert the_pattern.find_first("bfoo", 1) is null;

    match : the_pattern.find_first("a", 0);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_first("ayzzyby", 2);
    assert match2 is_not null;
    assert match2.begin == 5;
    assert match2.end == 6;
  }

  testcase test_find_last() {
    the_pattern : predicate_pattern[character].new(test_predicate);

    assert the_pattern.find_last("", missing.instance) is null;
    assert the_pattern.find_last("foo", missing.instance) is null;
    assert the_pattern.find_last("foo", 3) is null;
    assert the_pattern.find_last("fooc", 3) is null;

    match : the_pattern.find_last("c", 1);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_last("ayzzyby", 6);
    assert match2 is_not null;
    assert match2.begin == 5;
    assert match2.end == 6;

    match3 : the_pattern.find_last("ayzzyby", 4);
    assert match3 is_not null;
    assert match3.begin == 0;
    assert match3.end == 1;

    match4 : the_pattern.find_last("ayzzyby", missing.instance);
    assert match4 is_not null;
    assert match4.begin == 5;
    assert match4.end == 6;
  }

  testcase test_split() {
    the_pattern : predicate_pattern[character].new(test_predicate);

    split0: the_pattern.split("foo");
    assert split0.size == 1;
    assert equals(split0[0], "foo");

    split1: the_pattern.split("fooaxyzc");
    assert split1.size == 3;
    assert equals(split1[0], "foo");
    assert equals(split1[1], "xyz");
    assert equals(split1[2], "");

    split2: the_pattern.split("a1b2c3");
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

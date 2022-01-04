-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_singleton_pattern {

  test_case test_match() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern("x");
    assert !the_pattern("y");
    assert !the_pattern("xx");
  }

  test_case test_viable_prefix() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("x");
    assert !the_pattern.is_viable_prefix("y");
    assert !the_pattern.is_viable_prefix("xx");
  }

  test_case test_match_prefix() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern.match_prefix("") is null;
    assert the_pattern.match_prefix("x") == 1;
    assert the_pattern.match_prefix("y") is null;
    assert the_pattern.match_prefix("xx") == 1;
  }

  test_case test_find_first() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern.find_first("", 0) is null;
    assert the_pattern.find_first("foo", 0) is null;
    assert the_pattern.find_first("xfoo", 1) is null;

    match : the_pattern.find_first("x", 0);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_first("xyzzyxy", 2);
    assert match2 is_not null;
    assert match2.begin == 5;
    assert match2.end == 6;
  }

  test_case test_find_last() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern.find_last("", missing.instance) is null;
    assert the_pattern.find_last("foo", missing.instance) is null;
    assert the_pattern.find_last("foo", 3) is null;
    assert the_pattern.find_last("foox", 3) is null;

    match : the_pattern.find_last("x", 1);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_last("xyzzyxy", 6);
    assert match2 is_not null;
    assert match2.begin == 5;
    assert match2.end == 6;

    match3 : the_pattern.find_last("xyzzyxy", 4);
    assert match3 is_not null;
    assert match3.begin == 0;
    assert match3.end == 1;

    match4 : the_pattern.find_last("xyzzyxy", missing.instance);
    assert match4 is_not null;
    assert match4.begin == 5;
    assert match4.end == 6;
  }

  test_case test_split() {
    the_pattern : singleton_pattern[character].new('x');

    split0: the_pattern.split("foo");
    assert split0.size == 1;
    assert equals(split0[0], "foo");

    split1: the_pattern.split("fooxbarx");
    assert split1.size == 3;
    assert equals(split1[0], "foo");
    assert equals(split1[1], "bar");
    assert equals(split1[2], "");

    split2: the_pattern.split("x1x2x3");
    assert split2.size == 4;
    assert equals(split2[0], "");
    assert equals(split2[1], "1");
    assert equals(split2[2], "2");
    assert equals(split2[3], "3");
  }

  -- TODO: This hack shouldn't be needed.
  boolean equals(immutable list[character] s0, string s1) {
    -- deeply_immutable list[character] dil : s0;
    return (s0 !> string) == s1;
  }
}

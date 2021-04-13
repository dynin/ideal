-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_repeat_pattern {

  private boolean match_a(character c) pure {
    return c == 'a' || c == 'A';
  }

  private boolean match_b(character c) pure {
    return c == 'b' || c == 'B';
  }

  private boolean match_c(character c) pure {
    return c == 'c' || c == 'C';
  }

  repeat_pattern[character] make_pattern(boolean do_match_empty) {
    pattern[character] match_one_or_more_a : repeat_element[character].new(match_a, false);
    pattern[character] match_zero_or_more_b : repeat_element[character].new(match_b, true);
    pattern[character] match_one_or_more_c : repeat_element[character].new(match_c, false);

    patterns_list : [
        match_one_or_more_a,
        match_zero_or_more_b,
        match_one_or_more_c
    ];

    return repeat_pattern[character].new(sequence_pattern[character].new(patterns_list),
        do_match_empty);
  }

  testcase test_match() {
    the_pattern : make_pattern(true);

    assert the_pattern("");
    assert the_pattern("abc");
    assert the_pattern("AC");
    assert the_pattern("AaaCcc");
    assert the_pattern("AaaBBBCcc");
    assert the_pattern("abcabc");
    assert the_pattern("ACacABC");
    assert !the_pattern("bac");
    assert !the_pattern("aabb");
    assert !the_pattern("aaca");
    assert !the_pattern("aacabbbca");
  }

  testcase test_viable_prefix() {
    the_pattern : make_pattern(true);

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("a");
    assert the_pattern.is_viable_prefix("aAa");
    assert the_pattern.is_viable_prefix("aabb");
    assert the_pattern.is_viable_prefix("aacc");
    assert the_pattern.is_viable_prefix("aaBcc");
    assert the_pattern.is_viable_prefix("abcacab");
    assert the_pattern.is_viable_prefix("abbccaa");
    assert the_pattern.is_viable_prefix("ABCACAC");
    assert !the_pattern.is_viable_prefix("x");
    assert !the_pattern.is_viable_prefix("xyz");
    assert !the_pattern.is_viable_prefix("bbb");
    assert !the_pattern.is_viable_prefix("bcc");
    assert !the_pattern.is_viable_prefix("Ccc");
    assert !the_pattern.is_viable_prefix("Abcabcb");
  }

  testcase test_match_prefix() {
    the_pattern : make_pattern(false);

    assert the_pattern.match_prefix("") is null;
    assert the_pattern.match_prefix("a") is null;
    assert the_pattern.match_prefix("ab") is null;
    assert the_pattern.match_prefix("x") is null;
    assert the_pattern.match_prefix("xabc") is null;
    assert the_pattern.match_prefix("abc") == 3;
    assert the_pattern.match_prefix("abcdef") == 3;
    assert the_pattern.match_prefix("aAbBcCdef") == 6;
    assert the_pattern.match_prefix("aAbCdef") == 4;
    assert the_pattern.match_prefix("aaabbbcccddd") == 9;
    assert the_pattern.match_prefix("abcacaxyz") == 5;
    assert the_pattern.match_prefix("ACACABCfoo") == 7;
    assert the_pattern.match_prefix("ACfoo") == 2;

    the_pattern2 : make_pattern(true);

    assert the_pattern2.match_prefix("") == 0;
    assert the_pattern2.match_prefix("a") == 0;
    assert the_pattern2.match_prefix("ab") == 0;
    assert the_pattern2.match_prefix("x") == 0;
    assert the_pattern2.match_prefix("xabc") == 0;
    assert the_pattern2.match_prefix("abc") == 3;
    assert the_pattern2.match_prefix("abcdef") == 3;
    assert the_pattern2.match_prefix("aAbBcCdef") == 6;
    assert the_pattern2.match_prefix("aAbCdef") == 4;
    assert the_pattern2.match_prefix("aaabbbcccddd") == 9;
    assert the_pattern2.match_prefix("abcacaxyz") == 5;
    assert the_pattern2.match_prefix("ACACABCfoo") == 7;
    assert the_pattern2.match_prefix("ACfoo") == 2;
  }

  testcase test_find_first() {
    the_pattern : make_pattern(false);

    assert the_pattern.find_first("", 0) is null;
    assert the_pattern.find_first("foo", 0) is null;
    assert the_pattern.find_first("abcfoo", 3) is null;

    match : the_pattern.find_first("xxaabbcczz", 0);
    assert match is_not null;
    assert match.begin == 2;
    assert match.end == 8;

    match2 : the_pattern.find_first("xxabbbcccAAACCCBBB", 1);
    assert match2 is_not null;
    assert match2.begin == 2;
    assert match2.end == 15;

    match3 : the_pattern.find_first("fooACABCxyzABC", 0);
    assert match3 is_not null;
    assert match3.begin == 3;
    assert match3.end == 8;

    match4 : the_pattern.find_first("aaabbbCCCAAACCCBBB", 9);
    assert match4 is_not null;
    assert match4.begin == 9;
    assert match4.end == 15;
  }

  testcase test_split() {
    the_pattern : make_pattern(false);

    split0: the_pattern.split("foo");
    assert split0.size == 1;
    assert equals(split0[0], "foo");

    split1: the_pattern.split("fooACxyzABC");
    assert split1.size == 3;
    assert equals(split1[0], "foo");
    assert equals(split1[1], "xyz");
    assert equals(split1[2], "");

    split2: the_pattern.split("aaabc1ac2ABC3");
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

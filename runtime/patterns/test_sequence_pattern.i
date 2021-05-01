-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_sequence_pattern {

  private boolean match_a(character c) pure {
    return c == 'a' || c == 'A';
  }

  private boolean match_b(character c) pure {
    return c == 'b' || c == 'B';
  }

  private boolean match_c(character c) pure {
    return c == 'c' || c == 'C';
  }

  sequence_pattern[character] make_pattern() {
    pattern[character] match_one_or_more_a : repeat_element[character].new(match_a, false);
    pattern[character] match_zero_or_more_b : repeat_element[character].new(match_b, true);
    pattern[character] match_one_or_more_c : repeat_element[character].new(match_c, false);

    -- TODO: use array creation expression.
    list[pattern[character]] patterns_list : base_list[pattern[character]].new(
        match_one_or_more_a,
        match_zero_or_more_b,
        match_one_or_more_c
    );
    return sequence_pattern[character].new(patterns_list);
  }

  sequence_pattern[character] make_pattern2() {
    pattern[character] match_one_or_more_a : repeat_element[character].new(match_a, false);
    pattern[character] match_one_or_more_b : repeat_element[character].new(match_b, false);
    pattern[character] match_one_or_more_c : repeat_element[character].new(match_c, false);

    -- TODO: use array creation expression.
    list[pattern[character]] patterns_list : base_list[pattern[character]].new(
        match_one_or_more_a,
        match_one_or_more_b,
        match_one_or_more_c
    );
    return sequence_pattern[character].new(patterns_list);
  }

  test_case test_match() {
    the_pattern : make_pattern();

    assert the_pattern("abc");
    assert the_pattern("AC");
    assert the_pattern("AaaCcc");
    assert the_pattern("AaaBBBCcc");
    assert !the_pattern("bac");
    assert !the_pattern("aabb");
    assert !the_pattern("aaca");
  }

  test_case test_viable_prefix() {
    the_pattern : make_pattern();

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("a");
    assert the_pattern.is_viable_prefix("aAa");
    assert the_pattern.is_viable_prefix("aabb");
    assert the_pattern.is_viable_prefix("aacc");
    assert the_pattern.is_viable_prefix("aaBcc");
    assert !the_pattern.is_viable_prefix("x");
    assert !the_pattern.is_viable_prefix("xyz");
    assert !the_pattern.is_viable_prefix("bbb");
    assert !the_pattern.is_viable_prefix("bcc");
    assert !the_pattern.is_viable_prefix("Ccc");

    the_pattern2 : make_pattern2();

    assert the_pattern2.is_viable_prefix("");
    assert the_pattern2.is_viable_prefix("a");
    assert the_pattern2.is_viable_prefix("aAa");
    assert the_pattern2.is_viable_prefix("aabb");
    assert the_pattern2.is_viable_prefix("aaBcc");
    assert !the_pattern2.is_viable_prefix("x");
    assert !the_pattern2.is_viable_prefix("xyz");
    assert !the_pattern2.is_viable_prefix("bbb");
    assert !the_pattern2.is_viable_prefix("bcc");
    assert !the_pattern2.is_viable_prefix("Ccc");
  }

  test_case test_match_prefix() {
    the_pattern : make_pattern();

    assert the_pattern.match_prefix("") is null;
    assert the_pattern.match_prefix("a") is null;
    assert the_pattern.match_prefix("x") is null;
    assert the_pattern.match_prefix("xabc") is null;
    assert the_pattern.match_prefix("abc") == 3;
    assert the_pattern.match_prefix("abcdef") == 3;
    assert the_pattern.match_prefix("aAbBcCdef") == 6;
    assert the_pattern.match_prefix("aAbCdef") == 4;
    assert the_pattern.match_prefix("aaabbbcccddd") == 9;
  }

  test_case test_find_first() {
    the_pattern : make_pattern();

    assert the_pattern.find_first("", 0) is null;
    assert the_pattern.find_first("foo", 0) is null;
    assert the_pattern.find_first("abcfoo", 3) is null;

    match : the_pattern.find_first("xxaabbcczz", 0);
    assert match is_not null;
    assert match.begin == 2;
    assert match.end == 8;

    match2 : the_pattern.find_first("aaabbbAAACCCBBB", 1);
    assert match2 is_not null;
    assert match2.begin == 6;
    assert match2.end == 12;

    match3 : the_pattern.find_first("fooACxyzABC", 0);
    assert match3 is_not null;
    assert match3.begin == 3;
    assert match3.end == 5;
  }

  test_case test_find_first_more() {
    pattern[character] match_zero_or_more_b : repeat_element[character].new(match_b, true);
    pattern[character] match_one_or_more_c : repeat_element[character].new(match_c, false);

    pattern[character] beta :
        sequence_pattern[character].new([match_zero_or_more_b, match_one_or_more_c]);

    match : beta.find_first("aaabbbAAACCCBBB", 9);
    assert match is_not null;
    assert match.begin == 9;
    assert match.end == 12;
  }

  test_case test_split() {
    the_pattern : make_pattern();

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

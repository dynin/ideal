-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_option_pattern {

  private boolean match_a(character c) pure {
    return c == 'a' || c == 'A';
  }

  private boolean match_b(character c) pure {
    return c == 'b' || c == 'B';
  }

  private boolean match_c(character c) pure {
    return c == 'c' || c == 'C';
  }

  option_pattern[character] make_pattern() {
    pattern[character] match_one_or_more_a : repeat_element[character].new(match_a, false);
    pattern[character] match_zero_or_more_b : repeat_element[character].new(match_b, true);
    pattern[character] match_one_or_more_c : repeat_element[character].new(match_c, false);

    pattern[character] alpha :
        sequence_pattern[character].new([match_one_or_more_a, match_zero_or_more_b]);
    pattern[character] beta :
        sequence_pattern[character].new([match_zero_or_more_b, match_one_or_more_c]);

    return option_pattern[character].new([alpha, beta]);
  }

  testcase test_match() {
    the_pattern : make_pattern();

    assert the_pattern("ab");
    assert the_pattern("AAAA");
    assert the_pattern("AAAABB");
    assert the_pattern("bbc");
    assert the_pattern("BBCcc");
    assert the_pattern("Ccc");
    assert !the_pattern("abc");
    assert !the_pattern("baaaa");
    assert !the_pattern("accc");
  }

  testcase test_viable_prefix() {
    the_pattern : make_pattern();

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("a");
    assert the_pattern.is_viable_prefix("aAa");
    assert the_pattern.is_viable_prefix("aabb");
    assert the_pattern.is_viable_prefix("bb");
    assert the_pattern.is_viable_prefix("bbccc");
    assert the_pattern.is_viable_prefix("cc");
    assert !the_pattern.is_viable_prefix("x");
    assert !the_pattern.is_viable_prefix("xyz");
    assert !the_pattern.is_viable_prefix("ba");
    assert !the_pattern.is_viable_prefix("cca");
    assert !the_pattern.is_viable_prefix("Cccb");
  }

  testcase test_match_prefix() {
    the_pattern : make_pattern();

    assert the_pattern.match_prefix("") is null;
    assert the_pattern.match_prefix("x") is null;
    assert the_pattern.match_prefix("xac") is null;
    assert the_pattern.match_prefix("xab") is null;
    assert the_pattern.match_prefix("abbc") == 3;
    assert the_pattern.match_prefix("bcdef") == 2;
    assert the_pattern.match_prefix("aAbBcCdef") == 4;
    assert the_pattern.match_prefix("bCabc") == 2;
    assert the_pattern.match_prefix("aaabbbcccddd") == 6;
  }

  testcase test_find_first() {
    the_pattern : make_pattern();

    assert the_pattern.find_first("", 0) is null;
    assert the_pattern.find_first("foo", 0) is null;
    assert the_pattern.find_first("abcfoo", 3) is null;

    match : the_pattern.find_first("xxaabbcczz", 0);
    assert match is_not null;
    assert match.begin == 2;
    assert match.end == 6;

    match2 : the_pattern.find_first("aaabbbAAACCCBBB", 1);
    assert match2 is_not null;
    assert match2.begin == 1;
    assert match2.end == 6;

    match3 : the_pattern.find_first("aaabbbAAACCCBBB", 6);
    assert match3 is_not null;
    assert match3.begin == 6;
    assert match3.end == 9;

    match4 : the_pattern.find_first("fooACxyzABC", 0);
    assert match4 is_not null;
    assert match4.begin == 3;
    assert match4.end == 4;

    match5 : the_pattern.find_first("aaabbbAAACCCBBB", 9);
    assert match5 is_not null;
    assert match5.begin == 9;
    assert match5.end == 12;
  }

  testcase test_split() {
    the_pattern : make_pattern();

    split0: the_pattern.split("foo");
    assert split0.size == 1;
    assert equals(split0[0], "foo");

    split1: the_pattern.split("fooBCxyzAB");
    assert split1.size == 3;
    assert equals(split1[0], "foo");
    assert equals(split1[1], "xyz");
    assert equals(split1[2], "");

    split2: the_pattern.split("aaa1ab2BC3");
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

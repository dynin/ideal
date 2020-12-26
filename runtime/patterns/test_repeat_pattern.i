-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_repeat_pattern {

  private boolean test_predicate(character c) pure {
    return c == 'a' || c == 'b' || c == 'c';
  }

  testcase test_match() {
    zero_or_more : repeat_pattern[character].new(test_predicate, true);
    one_or_more : repeat_pattern[character].new(test_predicate, false);

    assert zero_or_more("");
    assert zero_or_more("a");
    assert zero_or_more("abca");
    assert !zero_or_more("abcda");
    assert !zero_or_more("y");
    assert !zero_or_more("xab");

    assert !one_or_more("");
    assert one_or_more("a");
    assert one_or_more("abca");
    assert !one_or_more("abcda");
    assert !one_or_more("y");
    assert !one_or_more("xab");
  }

  testcase test_viable_prefix() {
    zero_or_more : repeat_pattern[character].new(test_predicate, true);
    one_or_more : repeat_pattern[character].new(test_predicate, false);

    assert zero_or_more.is_viable_prefix("");
    assert zero_or_more.is_viable_prefix("a");
    assert zero_or_more.is_viable_prefix("ab");
    assert !zero_or_more.is_viable_prefix("y");
    assert !zero_or_more.is_viable_prefix("ay");

    assert one_or_more.is_viable_prefix("");
    assert one_or_more.is_viable_prefix("a");
    assert one_or_more.is_viable_prefix("ab");
    assert !one_or_more.is_viable_prefix("y");
    assert !one_or_more.is_viable_prefix("ay");
  }

  testcase test_match_prefix() {
    zero_or_more : repeat_pattern[character].new(test_predicate, true);
    one_or_more : repeat_pattern[character].new(test_predicate, false);

    assert zero_or_more.match_prefix("") == 0;
    assert zero_or_more.match_prefix("a") == 1;
    assert zero_or_more.match_prefix("abc") == 3;
    assert zero_or_more.match_prefix("abcdef") == 3;
    assert zero_or_more.match_prefix("x") == 0;
    assert zero_or_more.match_prefix("xabc") == 0;
    assert zero_or_more.match_prefix("abcabc") == 6;

    assert one_or_more.match_prefix("") is null;
    assert one_or_more.match_prefix("a") == 1;
    assert one_or_more.match_prefix("abc") == 3;
    assert one_or_more.match_prefix("abcdef") == 3;
    assert one_or_more.match_prefix("x") is null;
    assert one_or_more.match_prefix("xabc") is null;
    assert one_or_more.match_prefix("abcabc") == 6;
  }

  testcase test_find_first() {
    the_pattern : repeat_pattern[character].new(test_predicate, false);
    -- TODO: test pattern with do_match_empty.

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

  testcase test_find_last() {
    the_pattern : repeat_pattern[character].new(test_predicate, false);
    -- TODO: test pattern with do_match_empty.

    assert the_pattern.find_last("", missing.instance) is null;
    assert the_pattern.find_last("foo", missing.instance) is null;
    assert the_pattern.find_last("foo", 3) is null;
    assert the_pattern.find_last("fooc", 3) is null;

    match : the_pattern.find_last("c", 1);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_last("ayzzzby", 6);
    assert match2 is_not null;
    assert match2.begin == 5;
    assert match2.end == 6;

    match3 : the_pattern.find_last("abczyby", 4);
    assert match3 is_not null;
    assert match3.begin == 0;
    assert match3.end == 3;

    match4 : the_pattern.find_last("ayzzyabcy", missing.instance);
    assert match4 is_not null;
    assert match4.begin == 5;
    assert match4.end == 8;
  }

  testcase test_split() {
    the_pattern : repeat_pattern[character].new(test_predicate, false);

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

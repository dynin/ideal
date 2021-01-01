-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Match a sequence of patterns.
class sequence_pattern[readonly value element_type] {
  extends base_pattern[element_type];

  immutable list[pattern[element_type]] patterns_list;
  private mutable_var boolean validated;

  sequence_pattern(readonly list[pattern[element_type]] patterns_list) {
    this.patterns_list = patterns_list.frozen_copy();
  }

  implement void validate() {
    if (validated) {
      return;
    }
    validated = true;
    assert patterns_list.is_not_empty;
    for (the_pattern : patterns_list) {
      (the_pattern as validatable).validate();
    }
  }

  implement implicit boolean call(readonly list[element_type] the_list) {
    match : match_prefix(the_list);
    return match is_not null && match == the_list.size;
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    if (the_list.is_empty) {
      return true;
    }

    -- index in the |patterns_list|
    var nonnegative index : 0;
    -- length of the prefix in |the_list|
    var nonnegative prefix : 0;

    while (index < patterns_list.size - 1) {
      match : patterns_list[index].match_prefix(the_list.skip(prefix));
      if (match is null) {
        return false;
      }
      prefix += match;
      if (prefix == the_list.size) {
        return true;
      }
      index += 1;
    }

    assert index == patterns_list.size - 1;
    return patterns_list[index].is_viable_prefix(the_list.skip(prefix));
  }

  private nonnegative or null match_subsequence(readonly list[element_type] the_list,
      var nonnegative index, var nonnegative prefix) {
    while (index < patterns_list.size) {
      match : patterns_list[index].match_prefix(the_list.skip(prefix));
      if (match is null) {
        return missing.instance;
      }
      prefix += match;
      index += 1;
    }

    return prefix;
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    return match_subsequence(the_list, 0, 0);
  }

  implement range or null find_first(readonly list[element_type] the_list,
      var nonnegative start_index) {
    while (start_index <= the_list.size) {
      first_match : patterns_list[0].find_first(the_list, start_index);
      if (first_match is null) {
        return missing.instance;
      }
      rest_match : match_subsequence(the_list, 1, first_match.end);
      if (rest_match is_not null) {
        return base_range.new(first_match.begin, rest_match);
      }
      start_index = first_match.begin + 1;
    }
    return missing.instance;
  }
}

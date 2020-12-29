-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Match one of the collection of patterns.
class option_pattern[readonly value element_type] {
  extends base_pattern[element_type];

  protected list[pattern[element_type]] options;

  option_pattern(readonly collection[pattern[element_type]] options) {
    this.options = base_list[pattern[element_type]].new();
    this.options.append_all(options.elements);
  }

  void add_option(pattern[element_type] option) {
    options.append(option);
  }

  implement implicit boolean call(readonly list[element_type] the_list) {
    for (option : options) {
      if (option(the_list)) {
        return true;
      }
    }
    return false;
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    if (the_list.is_empty) {
      return true;
    }

    for (option : options) {
      if (option.is_viable_prefix(the_list)) {
        return true;
      }
    }
    return false;
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    var nonnegative or null result : missing.instance;

    for (option : options) {
      match : option.match_prefix(the_list);
      if (match is_not null) {
        -- Match the longest subsequence
        if (result is null || result < match) {
          result = match;
        }
      }
    }

    return result;
  }

  implement range or null find_first(readonly list[element_type] the_list,
      var nonnegative start_index) {
    var range or null result : missing.instance;

    for (option : options) {
      match : option.find_first(the_list, start_index);
      if (match is null) {
        continue;
      }
      if (result is null) {
        result = match;
      } else if (match.begin < result.begin ||
          (match.begin == result.begin && match.end > result.end)) {
        result = match;
      }
    }

    return result;
  }
}

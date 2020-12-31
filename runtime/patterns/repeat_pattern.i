-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Base pattern for matching a repeated pattern.
--- The pattern can't match an empty list.
class repeat_pattern[readonly value element_type] {
  extends base_pattern[element_type];

  pattern[element_type] the_pattern;
  boolean do_match_empty;

  repeat_pattern(pattern[element_type] the_pattern, boolean do_match_empty) {
    this.the_pattern = the_pattern;
    this.do_match_empty = do_match_empty;
    -- TODO: assert the_pattern doesn't match an empty list
  }

  implement implicit boolean call(readonly list[element_type] the_list) {
    var nonnegative index : 0;
    while (index < the_list.size) {
      match : the_pattern.match_prefix(the_list.skip(index));
      if (match is null) {
        return false;
      }
      assert match > 0;
      index += match;
    }
    return index > 0 || do_match_empty;
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    if (the_list.is_empty) {
      return true;
    }

    var nonnegative index : 0;
    while (index < the_list.size) {
      match : the_pattern.match_prefix(the_list.skip(index));
      if (match is null) {
        break;
      }
      assert match > 0;
      index += match;
    }

    return index == the_list.size || the_pattern.is_viable_prefix(the_list.skip(index));
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    var nonnegative index : 0;
    while (index < the_list.size) {
      match : the_pattern.match_prefix(the_list.skip(index));
      if (match is null) {
        break;
      }
      assert match > 0;
      index += match;
    }

    if (index == 0 && !do_match_empty) {
      return missing.instance;
    } else {
      return index;
    }
  }

  -- TODO: default start_index to 0.
  implement range or null find_first(readonly list[element_type] the_list,
      nonnegative start_index) {
    assert start_index <= the_list.size;
    if (do_match_empty) {
      if (start_index == the_list.size ||
          the_pattern.match_prefix(the_list.skip(start_index)) is null) {
        return base_range.new(start_index, start_index);
      }
    }

    for (var nonnegative i : start_index; i < the_list.size; i += 1) {
      match : the_pattern.match_prefix(the_list.skip(i));
      if (match is_not null) {
        start_range : i;
        assert match > 0;
        i += match;
        while (i < the_list.size) {
          next_match : the_pattern.match_prefix(the_list.skip(i));
          if (next_match is null) {
            break;
          }
          assert next_match > 0;
          i += next_match;
        }
        return base_range.new(start_range, i);
      }
    }

    return missing.instance;
  }
}

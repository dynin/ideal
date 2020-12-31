-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Base pattern for matching a repeated element.
abstract class base_repeat_element[readonly value element_type] {
  implements reversible_pattern[element_type];
  extends base_pattern[element_type];

  abstract boolean matches(element_type the_element);
  abstract boolean match_empty();

  implement implicit boolean call(readonly list[element_type] the_list) {
    var nonnegative index : 0;
    while (index < the_list.size) {
      if (!matches(the_list[index])) {
        return false;
      }
      index += 1;
    }
    return index > 0 || match_empty();
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    -- TODO: use list.has()
    for (the_element : the_list) {
      if (!matches(the_element)) {
        return false;
      }
    }
    return true;
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    var nonnegative index : 0;

    while (index < the_list.size && matches(the_list[index])) {
      index += 1;
    }

    if (index == 0 && !match_empty()) {
      return missing.instance;
    } else {
      return index;
    }
  }

  -- TODO: default start_index to 0.
  implement range or null find_first(readonly list[element_type] the_list,
      nonnegative start_index) {
    assert start_index <= the_list.size;
    if (match_empty()) {
      if (start_index == the_list.size || !matches(the_list[start_index])) {
        return base_range.new(start_index, start_index);
      }
    }

    for (var nonnegative i : start_index; i < the_list.size; i += 1) {
      if (matches(the_list[i])) {
        start_range : i;
        i += 1;
        while (i < the_list.size && matches(the_list[i])) {
          i += 1;
        }
        return base_range.new(start_range, i);
      }
    }

    return missing.instance;
  }

  -- TODO: default end_index to missing.
  implement range or null find_last(readonly list[element_type] the_list,
      nonnegative or null end_index) {
    var integer i;
    if (end_index is null) {
      i = the_list.size - 1;
    } else {
      assert end_index <= the_list.size;
      i = end_index - 1;
    }

    if (match_empty()) {
      if (i < 0) {
        return base_range.new(0, 0);
      } else {
        assert i is nonnegative;
        if (!matches(the_list[i])) {
          return base_range.new(i + 1, i + 1);
        }
      }
    }

    for (; i >= 0; i -= 1) {
      assert i is nonnegative;
      if (matches(the_list[i])) {
        end_range : i + 1;
        while (i > 0) {
          check_start : i - 1;
          assert check_start is nonnegative;
          if (!matches(the_list[check_start])) {
            break;
          }
          i = check_start;
        }
        return base_range.new(i, end_range);
      }
    }

    return missing.instance;
  }
}

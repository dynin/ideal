-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Base pattern for matching one element.
abstract class one_pattern[readonly value element_type] {
  implements reversible_pattern[element_type];
  extends base_pattern[element_type];

  abstract boolean matches(element_type the_element);

  implement implicit boolean call(readonly list[element_type] the_list) {
    return the_list.size == 1 && matches(the_list.first);
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    return the_list.is_empty || (the_list.size == 1 && matches(the_list.first));
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    if (the_list.is_not_empty && matches(the_list.first)) {
      return 1;
    } else {
      return missing.instance;
    }
  }

  -- TODO: default start_index to 0.
  implement range or null find_first(readonly list[element_type] the_list,
      nonnegative start_index) {
    for (var nonnegative i : start_index; i < the_list.size; i += 1) {
      if (matches(the_list[i])) {
        return base_range.new(i, i + 1);
      }
    }
    return missing.instance;
  }

  -- TODO: default end_index to missing.
  implement range or null find_last(readonly list[element_type] the_list,
      var nonnegative or null end_index) {
    var integer i;
    if (end_index is null) {
      i = the_list.size - 1;
    } else {
      assert end_index <= the_list.size;
      i = end_index - 1;
    }

    for (; i >= 0; i -= 1) {
      assert i is nonnegative;
      if (matches(the_list[i])) {
        return base_range.new(i, i + 1);
      }
    }

    return missing.instance;
  }
}

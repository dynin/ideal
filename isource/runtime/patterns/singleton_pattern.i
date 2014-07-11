-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Matches a single element.
class singleton_pattern[readonly equality_comparable element_type] {
  implements pattern[element_type];

  element_type the_element;

  singleton_pattern(element_type the_element) {
    this.the_element = the_element;
  }

  override implicit boolean call(readonly list[element_type] the_list) {
    return the_list.size == 1 && the_list[0] == the_element;
  }

  override boolean is_viable_prefix(readonly list[element_type] the_list) {
    return the_list.is_empty || (the_list.size == 1 && the_list[0] == the_element);
  }

  override range or null find_in(readonly list[element_type] the_list,
      nonnegative start_index) {
    for (var nonnegative i : start_index; i < the_list.size; i += 1) {
      if (the_list[i] == the_element) {
        return base_range.new(i, i + 1);
      }
    }
    return missing.instance;
  }

  override immutable list[immutable list[element_type]] split(
      immutable list[element_type] the_list) {

    result : base_list[immutable list[element_type]].new();
    var index : 0;

    loop {
      match : find_in(the_list, index);
      if (match is_not null) {
        result.append(the_list.slice(index, match.begin));
        index = match.end;
      } else {
        result.append(the_list.slice(index));
        break;
      }
    }

    return result.frozen_copy();
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Pattern that matches a list of elements.
class list_pattern[readonly equality_comparable element_type] {
  extends base_pattern[element_type];

  immutable list[element_type] element_list;

  list_pattern(readonly list[element_type] element_list) {
    this.element_list = element_list.frozen_copy;
  }

  implement implicit boolean call(readonly list[element_type] the_list) {
    return the_list.size == element_list.size && is_viable_prefix(the_list);
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    if (the_list.size > element_list.size) {
      return false;
    }
    for (var nonnegative index : 0; index < the_list.size; index += 1) {
      if (the_list[index] != element_list[index]) {
        return false;
      }
    }
    return true;
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    elements_size : element_list.size;
    if (the_list.size >= elements_size && this(the_list.slice(0, elements_size))) {
      return elements_size;
    } else {
      return missing.instance;
    }
  }

  implement range or null find_first(readonly list[element_type] the_list,
      var nonnegative start_index) {
    elements_size : element_list.size;
    var nonnegative index : start_index;

    while (elements_size + index <= the_list.size) {
      if (this(the_list.slice(index, index + elements_size))) {
        return base_range.new(index, index + elements_size);
      }
      index += 1;
    }

    return missing.instance;
  }

  implement validate() {
  }
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A matcher that combines a pattern and a parse procedure.
class procedure_matcher[readonly value element_type, any value result_type] {
  implements validatable, matcher[element_type, result_type];

  pattern[element_type] the_pattern;
  procedure[result_type, readonly list[element_type]] parser;

  procedure_matcher(pattern[element_type] the_pattern,
      procedure[result_type, readonly list[element_type]] parser) {
    this.the_pattern = the_pattern;
    this.parser = parser;
  }

  implement validate() {
    (the_pattern !> validatable).validate();
  }

  implement implicit boolean call(readonly list[element_type] the_list) {
    return the_pattern(the_list);
  }

  implement boolean is_viable_prefix(readonly list[element_type] the_list) {
    return the_pattern.is_viable_prefix(the_list);
  }

  implement nonnegative or null match_prefix(readonly list[element_type] the_list) {
    return the_pattern.match_prefix(the_list);
  }

  implement range or null find_first(readonly list[element_type] the_list,
      nonnegative start_index) {
    return the_pattern.find_first(the_list, start_index);
  }

  immutable list[immutable list[element_type]] split(immutable list[element_type] the_list) {
    return the_pattern.split(the_list);
  }

  implement result_type parse(readonly list[element_type] the_list) {
    return parser(the_list);
  }
}

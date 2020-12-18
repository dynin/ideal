-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Matches repeated element that conforms to a predicate.
class repeat_pattern[readonly equality_comparable element_type] {
  extends base_repeat_pattern[element_type];

  -- TODO: use the predicate type.
  private function[boolean, element_type] the_predicate;
  private boolean is_match_empty;

  repeat_pattern(function[boolean, element_type] the_predicate, boolean is_match_empty) {
    this.the_predicate = the_predicate;
    this.is_match_empty = is_match_empty;
  }

  implement boolean matches(element_type the_element) {
    return the_predicate(the_element);
  }

  implement boolean match_empty() {
    return is_match_empty;
  }
}

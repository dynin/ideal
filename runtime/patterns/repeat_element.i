-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Matches repeated element that conforms to a predicate.
class repeat_element[readonly equality_comparable element_type] {
  extends base_repeat_element[element_type];

  -- TODO: use the predicate type.
  private function[boolean, element_type] the_predicate;
  private boolean do_match_empty;

  repeat_element(function[boolean, element_type] the_predicate, boolean do_match_empty) {
    this.the_predicate = the_predicate;
    this.do_match_empty = do_match_empty;
  }

  implement boolean matches(element_type the_element) {
    return the_predicate(the_element);
  }

  implement boolean match_empty() {
    return do_match_empty;
  }
}

-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Matches a single element that conforms to a predicate.
class predicate_pattern[readonly equality_comparable element_type] {
  extends one_pattern[element_type];

  -- TODO: use the predicate type.
  function[boolean, element_type] the_predicate;

  predicate_pattern(function[boolean, element_type] the_predicate) {
    this.the_predicate = the_predicate;
  }

  implement boolean matches(element_type the_element) {
    return the_predicate(the_element);
  }
}

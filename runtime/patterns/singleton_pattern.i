-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Matches a single element.
class singleton_pattern[readonly equality_comparable element_type] {
  extends one_pattern[element_type];

  element_type singleton_element;

  singleton_pattern(element_type singleton_element) {
    this.singleton_element = singleton_element;
  }

  implement boolean matches(element_type the_element) {
    return the_element == singleton_element;
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

construct_data class list_construct {
  readonly list[construct] the_elements;
  grouping_type grouping;
  boolean has_trailing_comma;

  --- Simple grouping, such as (foo)
  -- TODO: make accessible as a field
  public boolean is_simple_grouping() {
    return the_elements.size == 1 && !has_trailing_comma;
  }
}

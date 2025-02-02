-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Immutable empty collection.
class empty[value element_type] {
  implements immutable list[element_type];
  implements immutable set[element_type];

  empty() { }

  implement nonnegative size => 0;

  implement boolean is_empty => true;

  implement boolean is_not_empty => false;

  implement element_type first() {
    utilities.panic("Can't access the first element of the empty list");
  }

  implement element_type last() {
    utilities.panic("Can't access the last element of the empty list");
  }

  implement boolean contains(element_type key) {
    return false;
  }

  implement implicit readonly reference[element_type] get(nonnegative index) pure {
    utilities.panic("Empty list");
  }

  implement immutable list[element_type] elements() {
    return this;
  }

  implement immutable empty[element_type] frozen_copy() {
    return this;
  }

  implement immutable list[element_type] skip(nonnegative count) {
    assert count == 0;
    return this;
  }

  implement immutable list[element_type] slice(nonnegative begin, nonnegative end) {
    assert begin == 0 && end == 0;
    return this;
  }

  implement immutable list[element_type] reversed() immutable {
    return this;
  }

  implement boolean has(predicate[element_type] the_predicate) pure {
    return false;
  }

  -- TODO: private static cached_indexes : base_range.new(0, 0);
  implement range indexes => base_range.new(0, 0);
}

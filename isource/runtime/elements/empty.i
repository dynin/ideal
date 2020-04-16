-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Immutable empty collection.
class empty[value element_type] {
  implements immutable list[element_type];
  implements immutable set[element_type];

  public empty() { }

  implement nonnegative size() {
    return 0;
  }

  implement boolean is_empty() {
    return true;
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

  implement immutable list[element_type] reverse() {
    return this;
  }
}

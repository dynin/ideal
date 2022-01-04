-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Immutable collection with one element.
class singleton_collection[readonly equality_comparable element_type] {
  implements immutable list[element_type];
  implements immutable set[element_type];

  element_type element;

  public singleton_collection(element_type element) {
    this.element = element;
  }

  implement nonnegative size => 1;

  implement boolean is_empty => false;

  implement boolean is_not_empty => true;

  implement element_type first => element;

  implement element_type last => element;

  implement boolean contains(element_type key) => key == element;

  implement implicit readonly reference[element_type] get(nonnegative index) pure {
    assert index == 0;
    return element;
  }

  implement immutable list[element_type] elements => this;

  implement immutable singleton_collection[element_type] frozen_copy => this;

  implement immutable list[element_type] skip(nonnegative count) {
    if (count == 0) {
      return this;
    } else {
      assert count == 1;
      return empty[element_type].new();
    }
  }

  implement immutable list[element_type] slice(nonnegative begin, nonnegative end) {
    if (begin == end) {
      return empty[element_type].new();
    } else {
      assert begin == 0 && end == 1;
      return this;
    }
  }

  implement immutable list[element_type] reversed => this;

  implement boolean has(predicate[element_type] the_predicate) pure {
    return the_predicate(element);
  }

  -- TODO: private static cached_indexes : base_range.new(0, 1);
  implement range indexes => base_range.new(0, 1);
}

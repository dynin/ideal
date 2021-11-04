-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_range {

  test_case test_empty() {
    the_range : base_range.new(68, 68);

    assert the_range.is_empty;
    assert the_range.size == 0;

    assert the_range.begin == 68;
    assert the_range.end == 68;

    new_range : the_range.slice(0, 0);

    assert new_range.is_empty;
    assert !new_range.is_not_empty;
    assert new_range.size == 0;

    assert new_range.begin == 68;
    assert new_range.end == 68;
  }

  test_case test_simple_range() {
    the_range : base_range.new(5, 8);

    assert the_range.begin == 5;
    assert the_range.end == 8;

    assert the_range.size == 3;
    assert !the_range.is_empty;
    assert the_range.is_not_empty;

    indexes : the_range.indexes;
    assert indexes.begin == 0;
    assert indexes.end == 3;

    assert the_range.first == 5;
    assert the_range.last == 7;

    assert the_range[0] == 5;
    assert the_range[1] == 6;
    assert the_range[2] == 7;

    assert the_range.elements == the_range;
    assert the_range.frozen_copy() == the_range;

    the_slice : the_range.skip(1);
    assert the_slice.size == 2;
    assert the_slice.begin == 6;
    assert the_slice.end == 8;
    assert the_slice.first == 6;
    assert the_slice.last == 7;

    the_slice2 : the_range.slice(1, 1);
    assert the_slice2.size == 0;
    assert the_slice2.is_empty;
    assert the_slice2.begin == 6;
    assert the_slice2.end == 6;

    reversed : the_range.reverse();
    assert reversed.size == 3;
    assert !reversed.is_empty;
    assert reversed.is_not_empty;
    assert reversed.first == 7;
    assert reversed.last == 5;
    assert reversed[0] == 7;
    assert reversed[1] == 6;
    assert reversed[2] == 5;
  }
}

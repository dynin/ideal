-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_range {

  testcase test_empty() {
    the_range : base_range.new(68, 68);

    assert the_range.is_empty;
    assert the_range.size == 0;

    assert the_range.begin == 68;
    assert the_range.end == 68;

    new_range : the_range.slice(0, 0);

    assert new_range.is_empty;
    assert new_range.size == 0;

    assert new_range.begin == 68;
    assert new_range.end == 68;
  }

  testcase test_simple_range() {
    the_range : base_range.new(5, 8);

    assert the_range.begin == 5;
    assert the_range.end == 8;

    assert the_range.size == 3;
    assert !the_range.is_empty;

    assert the_range[0] == 5;
    assert the_range[1] == 6;
    assert the_range[2] == 7;

    assert the_range.elements == the_range;
    assert the_range.frozen_copy() == the_range;

    the_slice : the_range.slice(1);
    assert the_slice.size == 2;
    assert the_slice.begin == 6;
    assert the_slice.end == 8;

    the_slice2 : the_range.slice(1, 1);
    assert the_slice2.size == 0;
    assert the_slice2.is_empty;
    assert the_slice2.begin == 6;
    assert the_slice2.end == 6;

    reversed : the_range.reverse();
    assert reversed.size == 3;
    assert !reversed.is_empty;
    assert reversed[0] == 7;
    assert reversed[1] == 6;
    assert reversed[2] == 5;
  }
}

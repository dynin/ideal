-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.machine.elements.array;

test_suite test_array {

  test_case test_creation() {
    the_array : array[string].new(10);
    assert the_array.size == 10;
  }

  test_case test_access() {
    the_array : array[string].new(10);

    the_array[5] = "foo";
    assert "foo" == the_array[5];
  }

  test_case test_move() {
    the_array : array[string].new(3);

    the_array[0] = "foo";
    the_array[1] = "bar";
    the_array[2] = "baz";

    assert "foo" == the_array[0];
    assert "bar" == the_array[1];
    assert "baz" == the_array[2];

    the_array.move(0, 1, 2);

    assert "foo" == the_array[0];
    assert "foo" == the_array[1];
    assert "bar" == the_array[2];
  }

  test_case test_initializer() {
    the_array : ["foo", "bar"];

    assert the_array.size == 2;
    assert the_array[0] == "foo";
    assert the_array[1] == "bar";
  }
}

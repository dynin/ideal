-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_list {

  testcase test_empty() {
    strings : empty[string].new();

    assert strings.is_empty;
    assert strings.size == 0;

    slice : strings.slice(0, 0);

    assert slice.is_empty;
    assert slice.size == 0;

    -- TODO: test exception throwing on out-of-bounds
  }

  testcase test_simple_list() {
    strings : base_list[string].new();

    assert strings.is_empty;
    assert strings.size == 0;

    strings.append("foo");

    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings[0] == "foo";

    strings.append("bar");

    assert strings.size == 2;
    assert !strings.is_empty;
    assert strings[0] == "foo";
    assert strings[1] == "bar";

    string removed : strings.remove_last();

    assert removed == "bar";
    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings[0] == "foo";

    -- TODO: test exception throwing on out-of-bounds
  }

  testcase test_list_elements() {
    strings : base_list[string].new();

    assert strings.is_empty;
    assert strings.size == 0;

    strings.append("foo");

    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings[0] == "foo";

    elements : strings.elements;
    assert elements.size == 1;
    assert !elements.is_empty;
    assert elements[0] == "foo";

    assert strings.size == 1;
    strings.append("bar");

    assert strings.size == 2;
    assert !strings.is_empty;
    assert strings[0] == "foo";
    assert strings[1] == "bar";
  }
}

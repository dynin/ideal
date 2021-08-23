-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_list {

  test_case test_empty() {
    strings : empty[string].new();

    assert strings.is_empty;
    assert strings.size == 0;

    slice : strings.slice(0, 0);

    assert slice.is_empty;
    assert !slice.is_not_empty;
    assert slice.size == 0;

    -- TODO: test exception throwing on out-of-bounds
  }

  test_case test_simple_list() {
    strings : base_list[string].new();

    assert strings.is_empty;
    assert strings.size == 0;

    strings.append("foo");

    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "foo";
    assert strings[0] == "foo";

    strings.append("bar");

    assert strings.size == 2;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "bar";
    assert strings[0] == "foo";
    assert strings[1] == "bar";

    string removed : strings.remove_last();

    assert removed == "bar";
    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "foo";
    assert strings[0] == "foo";

    -- TODO: test exception throwing on out-of-bounds
  }

  test_case test_list_elements() {
    strings : base_list[string].new();

    assert strings.is_empty;
    assert !strings.is_not_empty;
    assert strings.size == 0;

    strings.append("foo");

    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "foo";
    assert strings[0] == "foo";

    elements : strings.elements;
    assert elements.size == 1;
    assert !elements.is_empty;
    assert strings.is_not_empty;
    assert elements.first == "foo";
    assert elements.last == "foo";
    assert elements[0] == "foo";

    assert strings.size == 1;
    strings.append("bar");

    assert strings.size == 2;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "bar";
    assert strings[0] == "foo";
    assert strings[1] == "bar";
  }

  test_case test_list_remove() {
    strings : base_list[string].new();

    strings.append("foo");
    strings.append("bar");
    strings.append("baz");

    assert strings.size == 3;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "baz";
    assert strings[0] == "foo";
    assert strings[1] == "bar";
    assert strings[2] == "baz";

    strings.remove_at(1);

    assert strings.size == 2;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "foo";
    assert strings.last == "baz";
    assert strings[0] == "foo";
    assert strings[1] == "baz";

    strings.remove_at(0);

    assert strings.size == 1;
    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.first == "baz";
    assert strings.last == "baz";
    assert strings[0] == "baz";

    strings.remove_at(0);

    assert strings.size == 0;
    assert strings.is_empty;
    assert !strings.is_not_empty;
  }

  test_case test_list_sort() {
    numbers : base_list[integer].new();
    numbers.append_all([ -1, 68, 42, -2, 0]);

    numbers.sort(number_order.new());

    assert numbers.size == 5;
    assert numbers[0] == -2;
    assert numbers[1] == -1;
    assert numbers[2] == 0;
    assert numbers[3] == 42;
    assert numbers[4] == 68;
  }

  -- TODO: should be a singleton
  private class number_order {
    implements order[integer];

    implement implicit sign call(integer first, integer second) {
      return first <=> second;
    }
  }
}

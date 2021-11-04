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

    the range : strings.indexes;
    assert the_range.begin == 0;
    assert the_range.end == 0;

    -- TODO: test exception throwing on out-of-bounds
  }

  test_case test_singleton_collection() {
    strings : singleton_collection[string].new("foo");

    assert !strings.is_empty;
    assert strings.is_not_empty;
    assert strings.size == 1;

    the range : strings.indexes;
    assert the_range.begin == 0;
    assert the_range.end == 1;

    assert strings.first == "foo";
    assert strings.last == "foo";
    assert strings.contains("foo");
    assert !strings.contains("bar");
    assert strings[0] == "foo";

    strings_elements : strings.elements;
    assert strings_elements.size == 1;
    assert strings_elements[0] == "foo";

    strings_copy : strings.frozen_copy();
    assert strings_copy.size == 1;
    assert strings_copy[0] == "foo";

    skip0 : strings.skip(0);
    assert skip0.size == 1;
    assert skip0[0] == "foo";

    skip1 : strings.skip(1);
    assert skip1.size == 0;
    assert skip1.is_empty;

    slice0 : strings.slice(0, 0);
    assert slice0.is_empty;
    assert slice0.size == 0;

    slice1 : strings.slice(1, 1);
    assert slice1.is_empty;
    assert slice1.size == 0;

    slice01 : strings.slice(0, 1);
    assert slice01.is_not_empty;
    assert slice01.size == 1;
    assert slice01[0] == "foo";

    strings.reverse();
    assert strings.is_not_empty;
    assert strings.size == 1;
    assert strings[0] == "foo";

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

    the range : strings.indexes;
    assert the_range.begin == 0;
    assert the_range.end == 2;

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

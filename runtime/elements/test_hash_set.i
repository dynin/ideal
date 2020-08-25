-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Unittests for hash implementations of set.

class test_hash_set {

  testcase test_mutable_set() {
    set : hash_set[string].new();

    assert set.is_empty;
    assert !set.is_not_empty;
    assert set.size == 0;

    set2 : hash_set[string].new();
    set2.add("value");

    assert set2.size == 1;
    assert !set2.is_empty;
    assert set2.is_not_empty;
    assert set2.contains("value");
    assert !set2.contains("notfound");

    set2.add("value");
    assert set2.size == 1;
    assert !set2.is_empty;
    assert set2.is_not_empty;
    assert set2.contains("value");
    assert !set2.contains("notfound");

    set2.add("value2");
    assert set2.size == 2;
    assert !set2.is_empty;
    assert set2.is_not_empty;
    assert set2.contains("value");
    assert set2.contains("value2");
    assert !set2.contains("notfound");

    set3 : set2.frozen_copy();
    set2.add("value3");
    assert set2.size == 3;
    assert set3.size == 2;
    assert !set3.is_empty;
    assert set3.is_not_empty;
    assert set3.contains("value");
    assert set3.contains("value2");
    assert set2.contains("value3");
    assert !set3.contains("value3");

    removed2 : set2.remove("value2");
    assert removed2;
    assert set2.size == 2;
    assert set2.is_not_empty;
    assert set2.contains("value");
    assert !set2.contains("value2");
    assert set2.contains("value3");

    removed3 : set2.remove("value3");
    assert removed3;
    assert set2.size == 1;
    assert set2.is_not_empty;
    assert set2.contains("value");
    assert !set2.contains("value2");
    assert !set2.contains("value3");

    not_removed : set2.remove("foo");
    assert !not_removed;
    assert set2.size == 1;
    assert set2.is_not_empty;

    removed : set2.remove("value");
    assert removed;
    assert set2.is_empty;
    assert !set2.is_not_empty;
    assert set2.size == 0;
    assert !set2.contains("value");
    assert !set2.contains("value2");
    assert !set2.contains("value3");
  }

  testcase test_set_updates() {
    set : hash_set[string].new();
    for (var nonnegative max : 0; max < 68; max += 1) {
      set.add("v" ++ max);
      assert set.size == max + 1;
      for (var nonnegative i : 0; i <= max; i += 1) {
        assert set.contains("v" ++ i);
      }

      set_copy : set.frozen_copy();
      assert set_copy.size == max + 1;
      for (var nonnegative i : 0; i <= max; i += 1) {
        assert set_copy.contains("v" ++ i);
      }
    }
  }

  testcase test_set_add_all() {
    set1 : hash_set[string].new();
    set1.add("a");
    set1.add("b");
    set1.add("c");
    assert set1.size == 3;
    assert set1.contains("a");
    assert set1.contains("b");
    assert set1.contains("c");
    assert !set1.contains("d");

    set2 : hash_set[string].new();
    set2.add("b");
    set2.add("c");
    set2.add("d");
    assert set2.size == 3;
    assert !set2.contains("a");
    assert set2.contains("b");
    assert set2.contains("c");
    assert set2.contains("d");

    set1.add_all(set2);
    assert set1.size == 4;
    assert set1.contains("a");
    assert set1.contains("b");
    assert set1.contains("c");
    assert set1.contains("d");
    assert !set1.contains("e");
  }
}

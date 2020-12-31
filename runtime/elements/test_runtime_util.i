-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.elements.runtime_util;

class test_runtime_util {

  private class test1 {
    implements data, equality_comparable;

    public test1(integer i, string s) {
      this.i = i;
      this.s = s;
    }
    integer i;
    string s;
  }

  private test1 v1;
  private test1 v2;
  private test1 v3;
  private test1 v4;

  test_runtime_util() {
    v1 = test1.new(98, "asdf");
    v2 = test1.new(98, "asdf");
    v3 = test1.new(99, "asdf");
    v4 = test1.new(98, "asdfghj");
  }

  testcase test_class_name() {
    assert runtime_util.short_class_name(this) == "test_runtime_util";
    assert runtime_util.short_class_name("Hi") == "base_string";
  }

  testcase test_hash_code() {
    hash1 : runtime_util.compute_hash_code(v1);
    hash2 : runtime_util.compute_hash_code(v2);
    hash3 : runtime_util.compute_hash_code(v3);
    hash4 : runtime_util.compute_hash_code(v4);

    assert hash1 == hash2;
    assert hash1 != hash3;
    assert hash1 != hash4;
    assert hash3 != hash4;
  }

  testcase test_simple_equals() {
    assert runtime_util.values_equal(v1, v2);
    assert !runtime_util.values_equal(v1, v3);
    assert !runtime_util.values_equal(v1, v4);
    assert !runtime_util.values_equal(v4, v3);
    assert v1 != v3;
    assert v1 != v4;
    assert v4 != v3;
  }

  private immutable list[readonly data] make_list(readonly data first, readonly data second) {
    the_list : base_list[readonly data].new();
    the_list.append(first);
    the_list.append(second);
    return the_list.frozen_copy();
  }

  testcase test_list_equals() {
    s1 : make_list(v1, v3);
    s2 : make_list(v2, v3);
    s3 : make_list(v4, v3);

    assert runtime_util.values_equal(s1, s2);
    assert !runtime_util.values_equal(s1, s3);
  }

  testcase test_equals() {
    assert runtime_util.values_equal("Hi", "Hi");
    assert !runtime_util.values_equal("Hi", "Hello");
    assert runtime_util.values_equal(null, null);
    assert !runtime_util.values_equal(null, "foo");
    assert !runtime_util.values_equal("bar", null);
  }

  testcase test_escape() {
    assert runtime_util.escape_markup("foo") == "foo";
    assert runtime_util.escape_markup("hello! <>&") == "hello! &lt;&gt;&amp;";
    assert runtime_util.escape_markup("1: ' 2: \"") == "1: &apos; 2: &quot;";
  }
}

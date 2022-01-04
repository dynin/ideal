-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Unittests for implementations of dictionary.

test_suite test_dictionary {

  test_case test_mutable_dictionary() {
    dict : list_dictionary[string, string].new();

    assert dict.is_empty;
    assert dict.size == 0;

    dict2 : list_dictionary[string, string].new("key", "value");

    assert dict2.size == 1;
    assert !dict2.is_empty;
    assert dict2.is_not_empty;
    assert dict2.get("key") == "value";
    assert dict2.get("notfound") is null;

    dict2.put("key", "new_value");
    assert dict2.size == 1;
    assert !dict2.is_empty;
    assert dict2.is_not_empty;
    assert dict2.get("key") == "new_value";
    assert dict2.get("notfound") is null;

    dict2.put("key2", "bar");
    assert dict2.size == 2;
    assert !dict2.is_empty;
    assert dict2.is_not_empty;
    assert dict2.get("key") == "new_value";
    assert dict2.get("key2") == "bar";
    assert dict2.get("notfound") is null;

    dict3 : dict2.frozen_copy;
    dict2.put("key3", "baz");
    assert dict2.size == 3;
    assert dict3.size == 2;
    assert !dict3.is_empty;
    assert dict3.is_not_empty;
    assert dict3.get("key") == "new_value";
    assert dict3.get("key2") == "bar";
    assert dict3.get("notfound") is null;

    dict2.remove("key2");
    assert dict2.size == 2;
    assert dict2.get("key") == "new_value";
    assert dict2.get("key3") == "baz";
    assert dict2.get("key2") is null;
  }

  test_case test_immutable_dictionary() {
    dict : immutable_list_dictionary[string, string].new();

    assert dict.is_empty;
    assert !dict.is_not_empty;
    assert dict.size == 0;

    dict2 : immutable_list_dictionary[string, string].new("key", "value");

    assert dict2.size == 1;
    assert !dict2.is_empty;
    assert dict2.is_not_empty;
    assert dict2.get("key") == "value";
    assert dict2.get("notfound") is null;
  }
}

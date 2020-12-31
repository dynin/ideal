-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Unittests for hash implementations of dictionary.

class test_hash_dictionary {

  testcase test_mutable_dictionary() {
    dict : hash_dictionary[string, string].new();

    assert dict.is_empty;
    assert !dict.is_not_empty;
    assert dict.size == 0;

    dict2 : hash_dictionary[string, string].new();
    dict2.put("key", "value");

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

    dict3 : dict2.frozen_copy();
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

  testcase test_immutable_dictionary() {
    dict2 : hash_dictionary[string, string].new();
    dict2.put("key", "value");
    dict2i : dict2.frozen_copy();

    assert dict2i.size == 1;
    assert !dict2i.is_empty;
    assert dict2i.is_not_empty;
    assert dict2i.get("key") == "value";
    assert dict2i.get("notfound") is null;
  }

  testcase test_dictionary_updates() {
    dict : hash_dictionary[string, string].new();
    for (var nonnegative max : 0; max < 68; max += 1) {
      dict.put("k" ++ max, "v" ++ max);
      assert dict.size == max + 1;
      for (var nonnegative i : 0; i <= max; i += 1) {
        assert dict.get("k" ++ i) == "v" ++ i;
      }

      dict_copy : dict.frozen_copy();
      assert dict_copy.size == max + 1;
      for (var nonnegative i : 0; i <= max; i += 1) {
        assert dict_copy.get("k" ++ i) == "v" ++ i;
      }
    }
  }
}

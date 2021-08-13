-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementations of the core types of the library.
namespace elements {
  implicit import ideal.library.elements;

  interface has_equivalence;

  class empty;

  --- Base readonly list
  class base_readonly_list;
  class base_list;
  class base_immutable_list;

  class base_range;

  class debuggable;

  class base_string;

  class utilities;

  class base_dictionary_entry;
  class base_list_dictionary;
  class list_dictionary;
  class immutable_list_dictionary;

  class base_hash_dictionary;
  class hash_dictionary;
  class immutable_hash_dictionary;

  class base_hash_set;
  class hash_set;
  class immutable_hash_set;

  -- These are tests of runtime, move them
  test_suite test_array;
  test_suite test_runtime_util;
  test_suite test_string_writer;

  test_suite test_list;
  test_suite test_range;
  test_suite test_dictionary;
  test_suite test_hash_dictionary;
  test_suite test_hash_set;
}

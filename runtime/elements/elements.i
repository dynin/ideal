-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace elements {
  implicit import ideal.library.elements;

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
  class test_array;
  class test_runtime_util;
  class test_string_writer;

  class test_list;
  class test_range;
  class test_dictionary;
  class test_hash_dictionary;
  class test_hash_set;
}

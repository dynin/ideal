-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of serialization formats, currently using JSON.
---
--- Used https://www.json.org/ as a reference.
namespace formats {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.library.formats;
  implicit import ideal.runtime.elements;

  class json_array_impl {
    extends json_array;
    extends base_list[readonly json_data];
  }

  class json_object_impl {
    extends json_object;
    extends hash_dictionary[string, readonly json_data];
  }

  enum json_token;
  class json_parser;
  class json_printer;
  test_suite test_json_parser;
  test_suite test_json_printer;
}

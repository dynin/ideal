-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of serialization formats, currently using JSON.
---
--- Used https://www.json.org/ as a reference.
namespace formats {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.library.formats;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.characters;

  class json_array_impl;
  class json_object_impl;
  class json_object_list;
  enum json_token;
  class json_parser;
  class json_printer;
  test_suite test_json_parser;
  test_suite test_json_printer;
}

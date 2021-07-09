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
  implicit import ideal.runtime.elements;

  enum json_token;
  class json_parser;
  test_suite test_json_parser;
}

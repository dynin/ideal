-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class base_string_value {
  extends base_constant_value[string];
  implements string_value;

  base_string_value(string value, type bound) {
    super(value, bound);
  }

  -- TODO: the cast is redundant
  override string constant_to_string => (this .> string_value).unwrap;
}

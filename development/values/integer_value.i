-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class integer_value {
  extends base_constant_value[integer];
  implements mutable value_wrapper;

  integer_value(integer value, type bound) {
    super(value, bound);
  }

  override string constant_to_string() {
    return unwrap().to_string;
  }
}

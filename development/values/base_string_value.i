-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_string_value {
  extends base_constant_value[string];
  implements string_value;

  base_string_value(string value, type bound) {
    super(value, bound);
  }

  override string constant_to_string => unwrap();
}

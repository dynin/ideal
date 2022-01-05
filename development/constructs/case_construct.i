-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

meta_construct class case_construct {
  -- The value associated with this case statement, or null for default.
  construct or null case_value;

  override string to_string => utilities.describe(this, case_value);
}

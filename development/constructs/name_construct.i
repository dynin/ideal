-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

meta_construct class name_construct {
  implements stringable;

  action_name the_name;

  override string to_string => utilities.describe(this, the_name);
}

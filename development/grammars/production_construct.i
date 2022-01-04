-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

meta_construct class production_construct {
  implements stringable;

  name_construct the_name;
  readonly list[rule_construct] rules;

  override string to_string => utilities.describe(this, the_name);
}

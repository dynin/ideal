-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

meta_construct class production_construct {
  implements stringable;

  simple_name the_name;
  readonly list[readonly list[term_construct]] term_lists;

  override string to_string => utilities.describe(this, the_name);
}

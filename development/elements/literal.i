-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

interface literal[value value_type] {
  extends stringable, deeply_immutable data;

  value_type the_value;
}

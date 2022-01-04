-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

interface literal[value value_type] {
  extends stringable, deeply_immutable data;

  value_type the_value;
}

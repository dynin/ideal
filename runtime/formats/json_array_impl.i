-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of |json_array|.
class json_array_impl {
  extends json_array;
  extends base_list[readonly json_data];
}

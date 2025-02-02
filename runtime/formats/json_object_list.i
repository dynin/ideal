-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of |json_object| using a |list_dictionary|.
class json_object_list {
  extends json_object;
  extends list_dictionary[string, readonly json_data];
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of |json_object|.
class json_object_impl {
  extends json_object;
  extends hash_dictionary[string, readonly json_data];
}

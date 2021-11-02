-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class returned_value {
  extends jump_wrapper;

  entity_wrapper result;

  returned_value(entity_wrapper result) {
    this.result = result;
  }

  override string to_string => "return value: " ++ result;
}

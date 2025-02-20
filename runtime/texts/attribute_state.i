-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Helper data object for keeping track of attribute id/value pair.
-- TODO: use datatype.
class attribute_state {
  attribute_id id;
  attribute_fragment value;

  -- TODO: autogenerate constructor.
  attribute_state(attribute_id id, attribute_fragment value) {
    this.id = id;
    this.value = value;
  }
}

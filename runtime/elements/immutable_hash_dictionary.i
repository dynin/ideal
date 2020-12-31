-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.elements.runtime_util;

--- Mutable dictionary backed by a linked list.
public class immutable_hash_dictionary[readonly value key_type, value value_type] {

  extends base_hash_dictionary[key_type, value_type];
  implements immutable dictionary[key_type, value_type];

  protected immutable_hash_dictionary(equivalence_with_hash[key_type] equivalence,
      dictionary_state[key_type, value_type] state) {
    super(equivalence, state);
    state.writable = false;
  }

  implement immutable dictionary[key_type, value_type] frozen_copy() {
    return this;
  }
}

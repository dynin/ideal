-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.machine.elements.runtime_util;

--- Imnutable set backed by a hash set.
public class immutable_hash_set[readonly value element_type] {
  extends base_hash_set[element_type];
  implements immutable set[element_type];

  protected immutable_hash_set(equivalence_with_hash[element_type] equivalence,
      set_state[element_type] state) {
    super(equivalence, state);
    state.writable = false;
  }

  implement immutable set[element_type] frozen_copy() {
    return this;
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.elements.runtime_util;

--- Mutable dictionary backed by a linked list.
public class immutable_list_dictionary[readonly value key_type, value value_type] {

  extends base_list_dictionary[key_type, value_type];
  implements immutable dictionary[key_type, value_type];

  public overload immutable_list_dictionary(equivalence_relation[key_type] equivalence) {
    super(equivalence);
  }

  public overload immutable_list_dictionary() {
    -- TODO: cast is redundant.
    super(runtime_util.default_equivalence !> equivalence_relation[key_type]);
  }

  public overload immutable_list_dictionary(key_type the_key, value_type the_value) {
    -- TODO: cast is redundant.
    super(the_key, the_value, runtime_util.default_equivalence !> equivalence_relation[key_type]);
  }

  public overload immutable_list_dictionary(base_list_dictionary[key_type, value_type] original) {
    super(original);
  }

  implement immutable dictionary[key_type, value_type] frozen_copy() {
    return this;
  }
}

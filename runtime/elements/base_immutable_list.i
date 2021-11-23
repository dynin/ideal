-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_immutable_list[value element_type] {
  extends base_readonly_list[element_type];
  implements immutable list[element_type];

  import ideal.machine.elements.array;

  protected overload base_immutable_list(list_state[element_type] state) {
    super(state);
    state.writable = false;
  }

  public overload base_immutable_list(array[element_type] state) {
    super(list_state[element_type].new(state));
  }

  implement immutable list[element_type] frozen_copy() {
    return this;
  }

  implement immutable list[element_type] reversed() immutable {
    if (size <= 1) {
      return this;
    }

    reversed_state : list_state[element_type].new(size);
    for (var nonnegative i : 0; i < size; i += 1) {
      new_index : size - 1 - i;
      assert new_index is nonnegative;
      reversed_state.the_elements[new_index] = state.the_elements[i];
    }
    reversed_state.size = size;

    return base_immutable_list[element_type].new(reversed_state);
  }
}

-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_list[value element_type] {
  extends base_readonly_list[element_type];
  implements list[element_type];

  public overload base_list() {
  }

  public overload base_list(element_type n1) {
    this();
    append(n1);
  }

  public overload base_list(element_type n1, element_type n2) {
    this();
    append(n1);
    append(n2);
  }

  public overload base_list(element_type n1, element_type n2, element_type n3) {
    this();
    append(n1);
    append(n2);
    append(n3);
  }

  public overload base_list(element_type n1, element_type n2, element_type n3, element_type n4) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
  }

  public overload base_list(element_type n1, element_type n2, element_type n3, element_type n4,
      element_type n5) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
    append(n5);
  }

  public overload base_list(element_type n1, element_type n2, element_type n3, element_type n4,
      element_type n5, element_type n6) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
    append(n5);
    append(n6);
  }

  public overload base_list(element_type n1, element_type n2, element_type n3, element_type n4,
      element_type n5, element_type n6, element_type n7) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
    append(n5);
    append(n6);
    append(n7);
  }

  -- TODO: replace with a copy constructor
  public overload base_list(readonly list[element_type] the_list) {
    this();
    append_all(the_list);
  }

  implement void clear() {
    if (!is_empty) {
      state = list_state[element_type].new();
    }
  }

  private list_state[element_type] writable_state() {
    if (!state.writable) {
      state = state.copy();
      assert state.writable;
    }
    return state;
  }

  implement void append(element_type element) {
    writable_state().insert(size, element);
  }

  implement void append_all(readonly list[element_type] new_elements) {
    writable_state().insert_all(size, new_elements);
  }

  implement void prepend(element_type element) {
    writable_state().insert(0, element);
  }

  -- TODO: implicit shoudn't be necessary here.
  implement implicit mutable reference[element_type] at(nonnegative index) mutable pure {
    assert index < state.size;
    return writable_state().the_elements[index];
  }

  -- TODO: handle write refs
  -- writeonly reference[element_type] implicit at(nonnegative index)

  implement element_type remove_last() {
    assert !is_empty;
    last_index : size - 1;
    assert last_index is nonnegative;
    result : state.the_elements[last_index];
    writable_state().clear(last_index, 1);
    return result;
  }
}

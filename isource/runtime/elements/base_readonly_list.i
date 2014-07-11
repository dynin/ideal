-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import java.lang.Object;

--- A list is an ordered sequence of values.
class base_readonly_list[value element_type] {
  implements readonly list[element_type];

  namespace parameters {
    default_size : 16;
  }

  --- Wrapper of an |array| that implements on-demand resizing and copy-on-write semantics.
  --- The same |list_state| can be shared by multiple instances of |base_readonly_list|s.
  protected class list_state[value element_type] {

    import ideal.machine.elements.array;

    --- Specifies whether this instance of |list_state| is writable.
    --- A single non-writable copy can be shared among multiple instances of |base_readonly_list|.
    var boolean writable;

    --- An array used to store the elements.
    var array[element_type] the_elements;

    --- Specifies how many elements are stored in this |list_state|.
    --- The |size| is less or equal to |the_elements.size|.
    var nonnegative size;

    --- Construct a list state with an array of specified size.
    list_state(nonnegative initial_size) {
      writable = true;
      the_elements = array[element_type].new(initial_size);
      size = 0;
    }

    --- Construct a list state with an array of default size.
    list_state() {
      this(parameters.default_size);
    }

    --- Make sure the array is of at least the specified size.
    void reserve(nonnegative reserve_size) {
      if (the_elements.size >= reserve_size) {
        return;
      }

      var new_size : the_elements.size * 2;
      if (new_size < reserve_size) {
        new_size = reserve_size;
      }
      new_elements : array[element_type].new(new_size);
      the_elements.copy(0, new_elements, 0, size);
      the_elements = new_elements;
    }

    --- Insert elements at the specified index.
    void insert_all(nonnegative index, readonly list[element_type] new_elements) {
      if (new_elements.is_empty) {
        return;
      } else if (new_elements.size == 1) {
        insert(index, new_elements[0]);
        return;
      }

      assert writable;
      extra_size : new_elements.size;
      reserve_and_move(index, extra_size);
      new_elements_array : (new_elements as base_readonly_list[element_type]).state.the_elements;
      new_elements_array.copy(0, the_elements, index, extra_size);
    }

    --- Insert an element at the specified index.
    void insert(nonnegative index, element_type element) {
      assert writable;
      reserve_and_move(index, 1);
      the_elements[index] = element;
    }

    --- Helper method used to create space in the middle of an array.
    private void reserve_and_move(nonnegative index, nonnegative extra_size) {
      reserve(size + extra_size);
      if (index < size) {
        tail_size : size - index;
        assert tail_size is nonnegative;
        the_elements.move(index, index + extra_size, tail_size);
      }
      size += extra_size;
    }

    void clear(nonnegative begin, nonnegative length) {
      if (begin + length < size) {
        the_elements.move(begin + length, begin, length);
      }
      new_size : size - length;
      assert new_size is nonnegative;
      size = new_size;
      the_elements.scrub(size, length);
    }

    list_state[element_type] copy() {
      new_state : list_state[element_type].new(size);
      the_elements.copy(0, new_state.the_elements, 0, size);
      new_state.size = size;
      return new_state;
    }
  }

  protected var list_state[element_type] state;

  protected base_readonly_list() {
    state = list_state[element_type].new();
  }

  protected base_readonly_list(list_state[element_type] state) {
    this.state = state;
  }

  implement nonnegative size() {
    return state.size;
  }

  implement boolean is_empty() {
    return state.size == 0;
  }

  implement implicit readonly reference[element_type] get(nonnegative index) pure {
    assert index < state.size;
    return state.the_elements[index];
  }

  implement immutable list[element_type] elements() {
    return frozen_copy();
  }

  implement immutable list[element_type] frozen_copy() {
    return base_immutable_list[element_type].new(state);
  }

  implement immutable list[element_type] slice(nonnegative begin, nonnegative end) {
    assert begin >= 0 && end <= size;
    length : end - begin;
    assert length is nonnegative;
    slice_state : list_state[element_type].new(length);
    slice_state.size = length;
    state.the_elements.copy(begin, slice_state.the_elements, 0, length);
    return base_immutable_list[element_type].new(slice_state);
  }

  implement immutable list[element_type] slice(nonnegative begin) {
    return slice(begin, size);
  }
}
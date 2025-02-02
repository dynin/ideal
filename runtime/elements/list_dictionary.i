-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.machine.elements.runtime_util;

--- Mutable dictionary backed by a linked list.
public class list_dictionary[readonly value key_type, value value_type] {
  extends base_list_dictionary[key_type, value_type];
  implements dictionary[key_type, value_type];

  public overload list_dictionary(equivalence_relation[key_type] equivalence) {
    super(equivalence);
  }

  public overload list_dictionary() {
    -- TODO: cast is redundant.
    super(runtime_util.default_equivalence !> equivalence_relation[key_type]);
  }

  public overload list_dictionary(key_type the_key, value_type the_value) {
    -- TODO: cast is redundant.
    super(the_key, the_value, runtime_util.default_equivalence !> equivalence_relation[key_type]);
  }

  implement immutable dictionary[key_type, value_type] frozen_copy() {
    return immutable_list_dictionary[key_type, value_type].new(this);
  }

  implement clear() {
    the_size = 0;
    entries = missing.instance;
  }

  implement value_type or null put(key_type key, value_type value) {
    var entry : entries;
    if (entry is null) {
      entries = entry_cell[key_type, value_type].new(key, value);
      the_size = 1;
      return missing.instance;
    }

    loop {
      if (equivalence(key, entry.key)) {
        old_value : entry.value;
        entry.set_value(value);
        return old_value;
      }
      next : entry.next;
      if (next is null) {
        entry.next = entry_cell[key_type, value_type].new(key, value);
        the_size += 1;
        return missing.instance;
      } else {
        entry = next;
      }
    }
  }

  implement value_type or null remove(key_type key) {
    var entry : entries;
    if (entry is null) {
      return missing.instance;
    }

    if (equivalence(key, entry.key)) {
      old_value : entry.value;
      entries = entry.next;
      new_size : the_size - 1;
      assert new_size is nonnegative;
      the_size = new_size;
      return old_value;
    }

    loop {
      next_entry : entry.next;
      if (next_entry is null) {
        return missing.instance;
      }
      if (equivalence(key, next_entry.key)) {
        old_value : next_entry.value;
        entry.next = next_entry.next;
        new_size : the_size - 1;
        assert new_size is nonnegative;
        the_size = new_size;
        return old_value;
      }
      entry = next_entry;
    }
  }
}

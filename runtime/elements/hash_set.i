-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.elements.runtime_util;

--- Mutable dictionary backed by a hashtable.
public class hash_set[readonly value element_type] {
  extends base_hash_set[element_type];
  implements set[element_type];

  public overload hash_set(equivalence_with_hash[element_type] equivalence) {
    super(equivalence);
  }

  public overload hash_set() {
    -- TODO: cast is redundant.  Double cast is used to please Java compiler.
    super(runtime_util.default_equivalence !> any value !> equivalence_with_hash[element_type]);
  }

  private void copy_on_write() {
    if (!state.writable) {
      state = state.copy();
      assert state.writable;
    }
  }

  implement void clear() {
    copy_on_write();
    state.clear();
  }

  implement void add(element_type the_value) {
    copy_on_write();
    state.reserve(size + 1);
    do_add(the_value);
  }

  implement void add_all(readonly collection[element_type] the_collection) {
    -- TODO: reintroduce the reference equality check
    --if (the_collection == this) {
    --  return;
    --}

    copy_on_write();
    -- This may grow the hashtable more than needed...
    state.reserve(size + the_collection.size);
    for (the_element : the_collection.elements) {
      do_add(the_element);
    }
  }

  private void do_add(element_type the_value) {
    hash : equivalence.hash(the_value);
    index : state.bucket_index(hash);
    var entry : state.the_buckets[index];
    if (entry is null) {
      state.the_buckets[index] = hash_cell[element_type].new(the_value, hash);
      state.size += 1;
      return;
    }

    loop {
      if (hash == entry.the_hash && equivalence(the_value, entry.the_value)) {
        return;
      }
      next : entry.next;
      if (next is null) {
        entry.next = hash_cell[element_type].new(the_value, hash);
        state.size += 1;
        return;
      } else {
        entry = next;
      }
    }
  }

  implement boolean remove(element_type the_element) {
    copy_on_write();
    hash : equivalence.hash(the_element);
    index : state.bucket_index(hash);

    var entry : state.the_buckets[index];
    if (entry is null) {
      return false;
    }

    if (hash == entry.the_hash && equivalence(the_element, entry.the_value)) {
      state.the_buckets[index] = entry.next;
      decrement_size();
      return true;
    }

    loop {
      next : entry.next;
      if (next is null) {
        return false;
      }

      if (hash == next.the_hash && equivalence(the_element, next.the_value)) {
        entry.next = next.next;
        decrement_size();
        return true;
      }

      entry = next;
    }
  }

  private void decrement_size() {
    new_size : state.size - 1;
    assert new_size is nonnegative;
    state.size = new_size;
  }
}

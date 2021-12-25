-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.elements.runtime_util;

--- Mutable dictionary backed by a hashtable.
public class hash_dictionary[readonly value key_type, value value_type] {
  extends base_hash_dictionary[key_type, value_type];
  implements dictionary[key_type, value_type];

  public overload hash_dictionary(equivalence_with_hash[key_type] equivalence) {
    super(equivalence);
  }

  public overload hash_dictionary() {
    -- TODO: cast is redundant.
    super(runtime_util.default_equivalence !> equivalence_with_hash[key_type]);
  }

  private copy_on_write() {
    if (!state.writable) {
      state = state.copy();
      assert state.writable;
    }
  }

  implement clear() {
    copy_on_write();
    state.clear();
  }

  implement value_type or null put(key_type key, value_type value) {
    copy_on_write();
    state.reserve(size + 1);

    hash : equivalence.hash(key);
    index : state.bucket_index(hash);
    var entry : state.the_buckets[index];
    if (entry is null) {
      state.the_buckets[index] = hash_cell[key_type, value_type].new(key, hash, value);
      state.size += 1;
      return missing.instance;
    }

    loop {
      if (hash == entry.the_key_hash && equivalence(key, entry.key)) {
        old_value : entry.value;
        entry.set_value(value);
        return old_value;
      }
      next : entry.next;
      if (next is null) {
        entry.next = hash_cell[key_type, value_type].new(key, hash, value);
        state.size += 1;
        return missing.instance;
      } else {
        entry = next;
      }
    }
  }

  implement value_type or null remove(key_type key) {
    copy_on_write();

    hash : equivalence.hash(key);
    index : state.bucket_index(hash);
    var entry : state.the_buckets[index];
    if (entry is null) {
      return missing.instance;
    }

    if (hash == entry.the_key_hash && equivalence(key, entry.key)) {
      old_value : entry.value;
      state.the_buckets[index] = entry.next;
      new_size : state.size - 1;
      assert new_size is nonnegative;
      state.size = new_size;
      return old_value;
    }

    loop {
      next_entry : entry.next;
      if (next_entry is null) {
        return missing.instance;
      }
      if (hash == next_entry.the_key_hash && equivalence(key, next_entry.key)) {
        old_value : next_entry.value;
        entry.next = next_entry.next;
        new_size : state.size - 1;
        assert new_size is nonnegative;
        state.size = new_size;
        return old_value;
      }
      entry = next_entry;
    }
  }
}

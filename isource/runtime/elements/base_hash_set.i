-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Base class for the dictionary backed by a linked list.
public abstract class base_hash_set[readonly value element_type] {
  implements readonly set[element_type];

  namespace parameters {
    default_size : 16;
  }

  protected class hash_cell[readonly value element_type] {
    protected element_type the_value;
    protected integer the_hash;
    protected var hash_cell[element_type] or null next;

    public hash_cell(element_type the_value, integer the_hash,
        hash_cell[element_type] or null next) {
      this.the_value = the_value;
      this.the_hash = the_hash;
      this.next = next;
    }

    public hash_cell(element_type the_value, integer the_hash) {
      this(the_value, the_hash, missing.instance);
    }
  }

  --- Wrapper of an |array| that implements on-demand resizing and copy-on-write semantics.
  --- The same |set_state| can be shared by multiple instances of |base_hash_set|s.
  protected class set_state[readonly value element_type] {

    import ideal.machine.elements.array;

    --- Specifies whether this instance of |set_state| is writable.
    --- A single non-writable copy can be shared among multiple instances of |base_hash_set|.
    var boolean writable;

    --- An array used to store the elements.
    var array[hash_cell[element_type] or null] the_buckets;

    --- Specifies how many elements are stored in this |set_state|.
    --- The |size| is less or equal to |the_buckets.size|.
    var nonnegative size;

    --- Construct a dictionary state with an array of specified size.
    set_state(nonnegative initial_size) {
      writable = true;
      the_buckets = array[hash_cell[element_type] or null].new(initial_size);
      size = 0;
    }

    --- Construct a dictionary state with a table of default size.
    set_state() {
      this(parameters.default_size);
    }

    protected void clear() {
      if (size != 0) {
        the_buckets = array[hash_cell[element_type] or null].new(parameters.default_size);
        size = 0;
      }
    }

    --- Make sure the array is of at least the specified size.
    void reserve(nonnegative reserve_size) {
      if (the_buckets.size >= reserve_size) {
        return;
      }

      var new_size : the_buckets.size * 2;
      if (new_size < reserve_size) {
        new_size = reserve_size;
      }

      old_buckets : the_buckets;
      the_buckets = array[hash_cell[element_type] or null].new(new_size);

      -- TODO: use foreach iterator.
      for (var nonnegative i : 0; i < old_buckets.size; i += 1) {
        var bucket : old_buckets[i];
        while (bucket is_not null) {
          old_next : bucket.next;
          new_index : bucket_index(bucket.the_hash);
          bucket.next = the_buckets[new_index];
          the_buckets[new_index] = bucket;
          bucket = old_next;
        }
      }

      old_buckets.scrub(0, old_buckets.size);
    }

    protected nonnegative bucket_index(integer hash) {
      bucket_size : the_buckets.size;
      index : ((hash % bucket_size) + bucket_size) % bucket_size;
      assert index is nonnegative;
      return index;
    }

    protected set_state[element_type] copy() {
      result : set_state[element_type].new(the_buckets.size);

      -- TODO: use iterator.
      for (var nonnegative i : 0; i < the_buckets.size; i += 1) {
        var bucket : the_buckets[i];
        while (bucket is_not null) {
          new_cell : hash_cell[element_type].new(bucket.the_value, bucket.the_hash,
              result.the_buckets[i]);
          result.the_buckets[i] = new_cell;
          bucket = bucket.next;
        }
      }

      result.size = this.size;
      return result;
    }
  }

  protected equivalence_with_hash[element_type] equivalence;
  protected var set_state[element_type] state;

  protected base_hash_set(equivalence_with_hash[element_type] equivalence) {
    this.equivalence = equivalence;
    this.state = set_state[element_type].new();
  }

  protected base_hash_set(equivalence_with_hash[element_type] equivalence,
      set_state[element_type] state) {
    this.equivalence = equivalence;
    this.state = state;
  }

  implement nonnegative size() {
    return state.size;
  }

  implement boolean is_empty() {
    return state.size == 0;
  }

  implement immutable list[element_type] elements() {
    if (is_empty) {
      return empty[element_type].new();
    }
    result : base_list[element_type].new();
    for (var nonnegative i : 0; i < state.the_buckets.size; i += 1) {
      for (var entry : state.the_buckets[i]; entry is_not null; entry = entry.next) {
        result.append(entry.the_value);
      }
    }
    return result.frozen_copy();
  }

  implement immutable set[element_type] frozen_copy() {
    return immutable_hash_set[element_type].new(equivalence, state);
  }

  implement boolean contains(element_type key) {
    -- TODO: drop assert.
    assert key is_not null;
    hash : equivalence.hash(key);
    bucket : state.the_buckets[state.bucket_index(hash)];
    for (var entry : bucket; entry is_not null; entry = entry.next) {
      if (hash == entry.the_hash && equivalence(key, entry.the_value)) {
        return true;
      }
    }
    return false;
  }
}

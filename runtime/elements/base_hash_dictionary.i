-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Base class for the dictionary backed by a linked list.
public abstract class base_hash_dictionary[readonly value key_type, value value_type] {

  implements readonly dictionary[key_type, value_type];

  namespace parameters {
    default_size : 16;
  }

  protected class hash_cell[readonly value key_type, value value_type] {
    implements dictionary.entry[key_type, value_type];

    private key_type the_key;
    protected integer the_key_hash;
    private var value_type the_value;
    -- TODO: use self here
    protected var hash_cell[key_type, value_type] or null next;

    public overload hash_cell(key_type the_key, integer the_key_hash, value_type the_value,
        hash_cell[key_type, value_type] or null next) {
      this.the_key = the_key;
      this.the_key_hash = the_key_hash;
      this.the_value = the_value;
      this.next = next;
    }

    public overload hash_cell(key_type the_key, integer the_key_hash, value_type the_value) {
      this(the_key, the_key_hash, the_value, missing.instance);
    }

    override key_type key() {
      return the_key;
    }

    override value_type value() {
      return the_value;
    }

    protected void set_value(value_type new_value) {
      this.the_value = new_value;
    }
  }

  --- Wrapper of an |array| that implements on-demand resizing and copy-on-write semantics.
  --- The same |dictionary_state| can be shared by multiple instances of |base_hash_dictionary|s.
  protected class dictionary_state[readonly value key_type, value value_type] {

    import ideal.machine.elements.array;

    --- Specifies whether this instance of |dictionary_state| is writable.
    --- A single non-writable copy can be shared among multiple instances of |base_hash_dictionary|.
    var boolean writable;

    --- An array used to store the elements.
    var array[hash_cell[key_type, value_type] or null] the_buckets;

    --- Specifies how many elements are stored in this |dictionary_state|.
    --- The |size| is less or equal to |the_buckets.size|.
    var nonnegative size;

    --- Construct a dictionary state with an array of specified size.
    overload dictionary_state(nonnegative initial_size) {
      writable = true;
      the_buckets = array[hash_cell[key_type, value_type] or null].new(initial_size);
      size = 0;
    }

    --- Construct a dictionary state with a table of default size.
    overload dictionary_state() {
      this(parameters.default_size);
    }

    protected void clear() {
      if (size != 0) {
        the_buckets = array[hash_cell[key_type, value_type] or null].new(parameters.default_size);
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
      the_buckets = array[hash_cell[key_type, value_type] or null].new(new_size);

      -- TODO: use foreach iterator.
      for (var nonnegative i : 0; i < old_buckets.size; i += 1) {
        var bucket : old_buckets[i];
        while (bucket is_not null) {
          old_next : bucket.next;
          new_index : bucket_index(bucket.the_key_hash);
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

    protected dictionary_state[key_type, value_type] copy() {
      result : dictionary_state[key_type, value_type].new(the_buckets.size);

      -- TODO: use iterator.
      for (var nonnegative i : 0; i < the_buckets.size; i += 1) {
        var bucket : the_buckets[i];
        while (bucket is_not null) {
          new_cell : hash_cell[key_type, value_type].new(bucket.key, bucket.the_key_hash,
              bucket.value, result.the_buckets[i]);
          result.the_buckets[i] = new_cell;
          bucket = bucket.next;
        }
      }

      result.size = this.size;
      return result;
    }
  }

  protected equivalence_with_hash[key_type] equivalence;
  protected var dictionary_state[key_type, value_type] state;

  protected overload base_hash_dictionary(equivalence_with_hash[key_type] equivalence) {
    this.equivalence = equivalence;
    this.state = dictionary_state[key_type, value_type].new();
  }

  protected overload base_hash_dictionary(equivalence_with_hash[key_type] equivalence,
      dictionary_state[key_type, value_type] state) {
    this.equivalence = equivalence;
    this.state = state;
  }

  implement nonnegative size => state.size;

  implement boolean is_empty => state.size == 0;

  implement boolean is_not_empty => state.size != 0;

  implement immutable list[dictionary.entry[key_type, value_type]] elements() {
    if (is_empty) {
      return empty[dictionary.entry[key_type, value_type]].new();
    }
    result : base_list[dictionary.entry[key_type, value_type]].new();
    for (var nonnegative i : 0; i < state.the_buckets.size; i += 1) {
      for (var entry : state.the_buckets[i]; entry is_not null; entry = entry.next) {
        result.append(base_dictionary_entry[key_type, value_type].new(entry));
      }
    }
    return result.frozen_copy();
  }

  implement immutable dictionary[key_type, value_type] frozen_copy() {
    return immutable_hash_dictionary[key_type, value_type].new(equivalence, state);
  }

  private hash_cell[key_type, value_type] or null bucket(integer hash) {
    return state.the_buckets[state.bucket_index(hash)];
  }

  implement value_type or null get(key_type key) {
    -- TODO: drop assert.
    assert key is_not null;
    hash : equivalence.hash(key);
    for (var entry : bucket(hash); entry is_not null; entry = entry.next) {
      if (hash == entry.the_key_hash && equivalence(key, entry.key)) {
        return entry.value;
      }
    }
    return missing.instance;
  }

  implement boolean contains_key(key_type key) {
    -- TODO: drop assert.
    assert key is_not null;
    hash : equivalence.hash(key);
    for (var entry : bucket(hash); entry is_not null; entry = entry.next) {
      if (hash == entry.the_key_hash && equivalence(key, entry.key)) {
        return true;
      }
    }
    return false;
  }

  implement immutable set[key_type] keys() {
    if (is_empty) {
      -- TODO: use empty set constant
    }
    set[key_type] result : hash_set[key_type].new(equivalence);
    for (var nonnegative i : 0; i < state.the_buckets.size; i += 1) {
      for (var entry : state.the_buckets[i]; entry is_not null; entry = entry.next) {
        result.add(entry.key);
      }
    }
    return result.frozen_copy();
  }

  implement readonly collection[value_type] values() {
    list[value_type] result : base_list[value_type].new();
    for (var nonnegative i : 0; i < state.the_buckets.size; i += 1) {
      for (var entry : state.the_buckets[i]; entry is_not null; entry = entry.next) {
        result.append(entry.value);
      }
    }
    return result.frozen_copy();
  }

  private string debug_display() {
    var string s : "";
    for (var nonnegative i : 0; i < state.the_buckets.size; i += 1) {
      s = s ++ "b" ++ i ++ ": ";
      for (var entry : state.the_buckets[i]; entry is_not null; entry = entry.next) {
        s = s ++ "<" ++ (entry.key !> string) ++ ":" ++ (entry.value !> string) ++ ">";
      }
      s = s ++ "\n";
    }
    return s;
  }
}

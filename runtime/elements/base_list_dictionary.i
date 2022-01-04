-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Base class for the dictionary backed by a linked list.
public abstract class base_list_dictionary[readonly value key_type, value value_type] {

  implements readonly dictionary[key_type, value_type];

  protected class entry_cell[readonly value key_type, value value_type] {
    implements dictionary.entry[key_type, value_type];

    private key_type the_key;
    private var value_type the_value;
    -- TODO: use self here
    protected var entry_cell[key_type, value_type] or null next;

    public overload entry_cell(key_type the_key, value_type the_value,
        entry_cell[key_type, value_type] or null next) {
      this.the_key = the_key;
      this.the_value = the_value;
      this.next = next;
    }

    public overload entry_cell(key_type the_key, value_type the_value) {
      this(the_key, the_value, missing.instance);
    }

    override key_type key() {
      return the_key;
    }

    override value_type value() {
      return the_value;
    }

    protected set_value(value_type new_value) {
      this.the_value = new_value;
    }
  }

  protected equivalence_relation[key_type] equivalence;
  protected var nonnegative the_size;
  protected var entry_cell[key_type, value_type] or null entries;

  protected overload base_list_dictionary(equivalence_relation[key_type] equivalence) {
    this.equivalence = equivalence;
    the_size = 0;
    entries = missing.instance;
  }

  protected overload base_list_dictionary(key_type key, value_type value,
      equivalence_relation[key_type] equivalence) {
    this.equivalence = equivalence;
    the_size = 1;
    entries = entry_cell[key_type, value_type].new(key, value);
  }

  -- TODO: readonly dictionary
  protected overload base_list_dictionary(base_list_dictionary[key_type, value_type] original) {
    this.equivalence = original.equivalence;
    the_size = original.the_size;
    entries = duplicate(original.entries);
  }

  -- TODO: readonly original
  private entry_cell[key_type, value_type] or null duplicate(
      entry_cell[key_type, value_type] or null original) raw {
    if (original is null) {
      return original;
    }

    copy : entry_cell[key_type, value_type].new(original.key, original.value);
    var current : copy;
    var tail : original.next;
    while (tail is_not null) {
      cell_copy : entry_cell[key_type, value_type].new(tail.key, tail.value);
      current.next = cell_copy;
      current = cell_copy;
      tail = tail.next;
    }
    return copy;
  }

  implement nonnegative size => the_size;

  implement boolean is_empty => the_size == 0;

  implement boolean is_not_empty => the_size != 0;

  implement immutable list[dictionary.entry[key_type, value_type]] elements() {
    if (is_empty) {
      return empty[dictionary.entry[key_type, value_type]].new();
    }
    result : base_list[dictionary.entry[key_type, value_type]].new();
    for (var entry : entries; entry is_not null; entry = entry.next) {
      result.append(base_dictionary_entry[key_type, value_type].new(entry));
    }
    return result.frozen_copy;
  }

  implement value_type or null get(key_type key) {
    -- TODO: drop assert.
    assert key is_not null;
    for (var entry : entries; entry is_not null; entry = entry.next) {
      if (equivalence(key, entry.key)) {
        return entry.value;
      }
    }
    return missing.instance;
  }

  implement boolean contains_key(key_type key) {
    -- TODO: drop assert.
    assert key is_not null;
    for (var entry : entries; entry is_not null; entry = entry.next) {
      if (equivalence(key, entry.key)) {
        return true;
      }
    }
    return false;
  }

  implement immutable set[key_type] keys() {
    set[key_type] result :
        hash_set[key_type].new(equivalence !> equivalence_with_hash[key_type]);
    for (var entry : entries; entry is_not null; entry = entry.next) {
      result.add(entry.key);
    }
    return result.frozen_copy;
  }

  implement readonly collection[value_type] values() {
    list[value_type] result : base_list[value_type].new();
    for (var entry : entries; entry is_not null; entry = entry.next) {
      result.append(entry.value);
    }
    return result.frozen_copy;
  }
}

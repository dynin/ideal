// Autogenerated from isource/runtime/elements/list_dictionary.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.elements.runtime_util;

import javax.annotation.Nullable;

public class list_dictionary<key_type, value_type> extends base_list_dictionary<key_type, value_type> implements dictionary<key_type, value_type> {
  public list_dictionary(final equivalence_relation<key_type> equivalence) {
    super(equivalence);
  }
  public list_dictionary() {
    super((equivalence_relation<key_type>) runtime_util.default_equivalence);
  }
  public list_dictionary(final key_type the_key, final value_type the_value) {
    super(the_key, the_value, (equivalence_relation<key_type>) runtime_util.default_equivalence);
  }
  public @Override immutable_dictionary<key_type, value_type> frozen_copy() {
    return new immutable_list_dictionary<key_type, value_type>(this);
  }
  public @Override void clear() {
    the_size = 0;
    entries = null;
  }
  public @Override @Nullable value_type put(final key_type key, final value_type value) {
    @Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = entries;
    if (entry == null) {
      entries = new entry_cell<key_type, value_type>(key, value);
      the_size = 1;
      return null;
    }
    while (true) {
      if (equivalence.call(key, entry.key())) {
        final value_type old_value = entry.value();
        entry.set_value(value);
        return old_value;
      }
      final @Nullable base_list_dictionary.entry_cell<key_type, value_type> next = entry.next;
      if (next == null) {
        entry.next = new entry_cell<key_type, value_type>(key, value);
        the_size += 1;
        return null;
      } else {
        entry = next;
      }
    }
  }
  public @Override @Nullable value_type remove(final key_type key) {
    @Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = entries;
    if (entry == null) {
      return null;
    }
    if (equivalence.call(key, entry.key())) {
      final value_type old_value = entry.value();
      entries = entry.next;
      final int new_size = the_size - 1;
      assert new_size >= 0;
      the_size = new_size;
      return old_value;
    }
    while (true) {
      final @Nullable base_list_dictionary.entry_cell<key_type, value_type> next_entry = entry.next;
      if (next_entry == null) {
        return null;
      }
      if (equivalence.call(key, next_entry.key())) {
        final value_type old_value = next_entry.value();
        entry.next = next_entry.next;
        final int new_size = the_size - 1;
        assert new_size >= 0;
        the_size = new_size;
        return old_value;
      }
      entry = next_entry;
    }
  }
}
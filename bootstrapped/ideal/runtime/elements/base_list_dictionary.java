// Autogenerated from runtime/elements/base_list_dictionary.i

package ideal.runtime.elements;

import ideal.library.elements.*;

import javax.annotation.Nullable;

public abstract class base_list_dictionary<key_type, value_type> implements readonly_dictionary<key_type, value_type> {
  protected static class entry_cell<key_type, value_type> implements dictionary.entry<key_type, value_type> {
    private final key_type the_key;
    private value_type the_value;
    protected @Nullable base_list_dictionary.entry_cell<key_type, value_type> next;
    public entry_cell(final key_type the_key, final value_type the_value, final @Nullable base_list_dictionary.entry_cell<key_type, value_type> next) {
      this.the_key = the_key;
      this.the_value = the_value;
      this.next = next;
    }
    public entry_cell(final key_type the_key, final value_type the_value) {
      this(the_key, the_value, null);
    }
    public @Override key_type key() {
      return this.the_key;
    }
    public @Override value_type value() {
      return this.the_value;
    }
    protected void set_value(final value_type new_value) {
      this.the_value = new_value;
    }
  }
  protected final equivalence_relation<key_type> equivalence;
  protected Integer the_size;
  protected @Nullable base_list_dictionary.entry_cell<key_type, value_type> entries;
  protected base_list_dictionary(final equivalence_relation<key_type> equivalence) {
    this.equivalence = equivalence;
    this.the_size = 0;
    this.entries = null;
  }
  protected base_list_dictionary(final key_type key, final value_type value, final equivalence_relation<key_type> equivalence) {
    this.equivalence = equivalence;
    this.the_size = 1;
    this.entries = new base_list_dictionary.entry_cell<key_type, value_type>(key, value);
  }
  protected base_list_dictionary(final base_list_dictionary<key_type, value_type> original) {
    this.equivalence = original.equivalence;
    this.the_size = original.the_size;
    this.entries = this.duplicate(original.entries);
  }
  private @Nullable base_list_dictionary.entry_cell<key_type, value_type> duplicate(final @Nullable base_list_dictionary.entry_cell<key_type, value_type> original) {
    if (original == null) {
      return original;
    }
    final base_list_dictionary.entry_cell<key_type, value_type> copy = new base_list_dictionary.entry_cell<key_type, value_type>(original.key(), original.value());
    base_list_dictionary.entry_cell<key_type, value_type> current = copy;
    @Nullable base_list_dictionary.entry_cell<key_type, value_type> tail = original.next;
    while (tail != null) {
      final base_list_dictionary.entry_cell<key_type, value_type> cell_copy = new base_list_dictionary.entry_cell<key_type, value_type>(tail.key(), tail.value());
      current.next = cell_copy;
      current = cell_copy;
      tail = tail.next;
    }
    return copy;
  }
  public @Override Integer size() {
    return this.the_size;
  }
  public @Override boolean is_empty() {
    return ideal.machine.elements.runtime_util.values_equal(this.the_size, 0);
  }
  public @Override boolean is_not_empty() {
    return !ideal.machine.elements.runtime_util.values_equal(this.the_size, 0);
  }
  public @Override immutable_list<dictionary.entry<key_type, value_type>> elements() {
    if (this.is_empty()) {
      return new empty<dictionary.entry<key_type, value_type>>();
    }
    final base_list<dictionary.entry<key_type, value_type>> result = new base_list<dictionary.entry<key_type, value_type>>();
    for (@Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = this.entries; entry != null; entry = entry.next) {
      result.append(new base_dictionary_entry<key_type, value_type>(entry));
    }
    return result.frozen_copy();
  }
  public @Override @Nullable value_type get(final key_type key) {
    assert key != null;
    for (@Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = this.entries; entry != null; entry = entry.next) {
      if (((function2<Boolean, key_type, key_type>) (Object) this.equivalence).call(key, entry.key())) {
        return entry.value();
      }
    }
    return null;
  }
  public @Override boolean contains_key(final key_type key) {
    assert key != null;
    for (@Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = this.entries; entry != null; entry = entry.next) {
      if (((function2<Boolean, key_type, key_type>) (Object) this.equivalence).call(key, entry.key())) {
        return true;
      }
    }
    return false;
  }
  public @Override immutable_set<key_type> keys() {
    final set<key_type> result = new hash_set<key_type>((equivalence_with_hash<key_type>) (Object) this.equivalence);
    for (@Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = this.entries; entry != null; entry = entry.next) {
      result.add(entry.key());
    }
    return result.frozen_copy();
  }
  public @Override readonly_collection<value_type> values() {
    final list<value_type> result = new base_list<value_type>();
    for (@Nullable base_list_dictionary.entry_cell<key_type, value_type> entry = this.entries; entry != null; entry = entry.next) {
      result.append(entry.value());
    }
    return result.frozen_copy();
  }
}

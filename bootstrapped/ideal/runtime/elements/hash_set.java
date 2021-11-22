// Autogenerated from runtime/elements/hash_set.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.elements.runtime_util;

import javax.annotation.Nullable;

public class hash_set<element_type> extends base_hash_set<element_type> implements set<element_type> {
  public hash_set(final equivalence_with_hash<element_type> equivalence) {
    super(equivalence);
  }
  public hash_set() {
    super((equivalence_with_hash<element_type>) runtime_util.default_equivalence);
  }
  private void copy_on_write() {
    if (!this.state.writable) {
      this.state = this.state.copy();
      assert this.state.writable;
    }
  }
  public @Override void clear() {
    this.copy_on_write();
    this.state.clear();
  }
  public @Override void add(final element_type the_value) {
    this.copy_on_write();
    this.state.reserve(this.size() + 1);
    this.do_add(the_value);
  }
  public @Override void add_all(final readonly_collection<element_type> the_collection) {
    this.copy_on_write();
    this.state.reserve(this.size() + the_collection.size());
    {
      final readonly_list<element_type> the_element_list = the_collection.elements();
      for (Integer the_element_index = 0; the_element_index < the_element_list.size(); the_element_index += 1) {
        final element_type the_element = the_element_list.get(the_element_index);
        this.do_add(the_element);
      }
    }
  }
  private void do_add(final element_type the_value) {
    final Integer hash = this.equivalence.hash(the_value);
    final Integer index = this.state.bucket_index(hash);
    @Nullable base_hash_set.hash_cell<element_type> entry = this.state.the_buckets.at(index).get();
    if (entry == null) {
      this.state.the_buckets.set(index, new base_hash_set.hash_cell<element_type>(the_value, hash));
      this.state.size += 1;
      return;
    }
    while (true) {
      if (runtime_util.values_equal(hash, entry.the_hash) && ((function2<Boolean, element_type, element_type>) (Object) this.equivalence).call(the_value, entry.the_value)) {
        return;
      }
      final @Nullable base_hash_set.hash_cell<element_type> next = entry.next;
      if (next == null) {
        entry.next = new base_hash_set.hash_cell<element_type>(the_value, hash);
        this.state.size += 1;
        return;
      } else {
        entry = next;
      }
    }
  }
  public @Override boolean remove(final element_type the_element) {
    this.copy_on_write();
    final Integer hash = this.equivalence.hash(the_element);
    final Integer index = this.state.bucket_index(hash);
    @Nullable base_hash_set.hash_cell<element_type> entry = this.state.the_buckets.at(index).get();
    if (entry == null) {
      return false;
    }
    if (runtime_util.values_equal(hash, entry.the_hash) && ((function2<Boolean, element_type, element_type>) (Object) this.equivalence).call(the_element, entry.the_value)) {
      this.state.the_buckets.set(index, entry.next);
      this.decrement_size();
      return true;
    }
    while (true) {
      final @Nullable base_hash_set.hash_cell<element_type> next = entry.next;
      if (next == null) {
        return false;
      }
      if (runtime_util.values_equal(hash, next.the_hash) && ((function2<Boolean, element_type, element_type>) (Object) this.equivalence).call(the_element, next.the_value)) {
        entry.next = next.next;
        this.decrement_size();
        return true;
      }
      entry = next;
    }
  }
  private void decrement_size() {
    final Integer new_size = this.state.size - 1;
    assert new_size >= 0;
    this.state.size = new_size;
  }
}

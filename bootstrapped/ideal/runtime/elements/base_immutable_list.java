// Autogenerated from isource/runtime/elements/base_immutable_list.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.elements.runtime_util;

public class base_immutable_list<element_type> extends base_readonly_list<element_type> implements immutable_list<element_type> {
  protected base_immutable_list(final list_state<element_type> state) {
    super(state);
    state.writable = false;
  }
  public @Override immutable_list<element_type> frozen_copy() {
    return this;
  }
  public @Override immutable_list<element_type> reverse() {
    if (size() <= 1) {
      return this;
    }
    final base_readonly_list.list_state<element_type> reversed = new list_state<element_type>(size());
    for (int i = 0; i < size(); i += 1) {
      final int new_index = size() - 1 - i;
      assert new_index >= 0;
      reversed.the_elements.set(new_index, state.the_elements.at(i).get());
    }
    reversed.size = size();
    return new base_immutable_list<element_type>(reversed);
  }
}
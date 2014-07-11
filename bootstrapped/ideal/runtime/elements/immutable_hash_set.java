// Autogenerated from isource/runtime/elements/immutable_hash_set.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.elements.runtime_util;

public class immutable_hash_set<element_type> extends base_hash_set<element_type> implements immutable_set<element_type> {
  protected immutable_hash_set(final equivalence_with_hash<element_type> equivalence, final set_state<element_type> state) {
    super(equivalence, state);
    state.writable = false;
  }
  public @Override immutable_set<element_type> frozen_copy() {
    return this;
  }
}

// Autogenerated from runtime/elements/immutable_list_dictionary.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.elements.runtime_util;

public class immutable_list_dictionary<key_type, value_type> extends base_list_dictionary<key_type, value_type> implements immutable_dictionary<key_type, value_type> {
  public immutable_list_dictionary(final equivalence_relation<key_type> equivalence) {
    super(equivalence);
  }
  public immutable_list_dictionary() {
    super((equivalence_relation<key_type>) (Object) runtime_util.default_equivalence);
  }
  public immutable_list_dictionary(final key_type the_key, final value_type the_value) {
    super(the_key, the_value, (equivalence_relation<key_type>) (Object) runtime_util.default_equivalence);
  }
  public immutable_list_dictionary(final base_list_dictionary<key_type, value_type> original) {
    super(original);
  }
  public @Override immutable_dictionary<key_type, value_type> frozen_copy() {
    return this;
  }
}

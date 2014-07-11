// Autogenerated from isource/runtime/elements/base_dictionary_entry.i

package ideal.runtime.elements;

import ideal.library.elements.*;

public class base_dictionary_entry<key_type, value_type> implements dictionary.entry<key_type, value_type> {
  private final key_type the_key;
  private final value_type the_value;
  public base_dictionary_entry(final dictionary.entry<key_type, value_type> entry) {
    this.the_key = entry.key();
    this.the_value = entry.value();
  }
  public @Override key_type key() {
    return the_key;
  }
  public @Override value_type value() {
    return the_value;
  }
}
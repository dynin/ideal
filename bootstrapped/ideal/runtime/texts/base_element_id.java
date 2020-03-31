// Autogenerated from isource/runtime/texts/base_element_id.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

public class base_element_id extends debuggable implements element_id, reference_equality {
  private final text_namespace the_namespace;
  private final string name;
  public base_element_id(final text_namespace the_namespace, final string name) {
    this.the_namespace = the_namespace;
    this.name = name;
  }
  public @Override text_namespace get_namespace() {
    return the_namespace;
  }
  public @Override string short_name() {
    return name;
  }
  public @Override string to_string() {
    return ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(the_namespace.to_string(), new base_string(":")), name);
  }
}

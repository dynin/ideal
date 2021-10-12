// Autogenerated from development/elements/access_modifier.i

package ideal.development.elements;

import ideal.library.elements.*;
import ideal.runtime.elements.debuggable;
import ideal.runtime.logs.*;

public class access_modifier extends debuggable implements modifier_kind, readonly_displayable {
  public static final access_modifier public_modifier = new access_modifier(new ideal.runtime.elements.base_string("public"));
  public static final access_modifier private_modifier = new access_modifier(new ideal.runtime.elements.base_string("private"));
  public static final access_modifier protected_modifier = new access_modifier(new ideal.runtime.elements.base_string("protected"));
  public static final access_modifier local_modifier = new access_modifier(new ideal.runtime.elements.base_string("local"));
  private final simple_name the_name;
  private access_modifier(final string name) {
    this.the_name = simple_name.make(name);
  }
  public @Override simple_name name() {
    return this.the_name;
  }
  public @Override string to_string() {
    return ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(new ideal.runtime.elements.base_string("<"), this.the_name.to_string()), new ideal.runtime.elements.base_string(">"));
  }
  public @Override string display() {
    return this.to_string();
  }
}

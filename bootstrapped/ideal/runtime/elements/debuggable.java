// Autogenerated from isource/runtime/elements/debuggable.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import java.lang.String;
import java.lang.Object;

public class debuggable extends Object implements convertible_to_string {
  protected debuggable() { }
  public @Override string to_string() {
    return utilities.describe(this);
  }
  public @Override final String toString() {
    return utilities.s(to_string());
  }
}

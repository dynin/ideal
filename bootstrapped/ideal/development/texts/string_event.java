// Autogenerated from development/texts/string_event.i

package ideal.development.texts;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;

public class string_event implements text_event {
  public final string payload;
  public string_event(final string payload) {
    this.payload = payload;
  }
  public @Override string to_string() {
    return payload;
  }
}
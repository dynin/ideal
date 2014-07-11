// Autogenerated from isource/runtime/logs/simple_message.i

package ideal.runtime.logs;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;

public class simple_message implements log_message {
  private final log_level the_level;
  private final string the_string;
  public simple_message(final log_level the_level, final string the_string) {
    this.the_level = the_level;
    this.the_string = the_string;
  }
  public @Override log_level level() {
    return the_level;
  }
  public @Override string to_string() {
    return the_string;
  }
  public @Override text_fragment to_text() {
    if (the_string.is_empty()) {
      return new base_element(text_library.BR);
    } else {
      return base_element.make(text_library.DIV, (base_string) the_string);
    }
  }
}

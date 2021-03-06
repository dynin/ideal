// Autogenerated from development/elements/notification.i

package ideal.development.elements;

import ideal.library.elements.*;
import ideal.library.texts.text_fragment;
import ideal.library.messages.log_message;

public interface notification extends log_message {
  string message();
  origin origin();
  readonly_list<notification> secondary();
  text_fragment render_text(boolean prefix_with_source);
  void report();
}

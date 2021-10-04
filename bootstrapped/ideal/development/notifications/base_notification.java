// Autogenerated from development/notifications/base_notification.i

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.origins.*;

import javax.annotation.Nullable;

public class base_notification implements notification {
  private final string the_message;
  private final origin the_origin;
  private final @Nullable readonly_list<notification> the_secondary;
  public base_notification(final string the_message, final origin the_origin, final @Nullable readonly_list<notification> the_secondary) {
    assert the_message != null;
    assert the_origin != null;
    this.the_message = the_message;
    this.the_origin = the_origin;
    this.the_secondary = (the_secondary != null && the_secondary.is_not_empty()) ? the_secondary : null;
  }
  public base_notification(final string the_message, final origin the_origin) {
    this(the_message, the_origin, null);
  }
  public @Override string message() {
    return this.the_message;
  }
  public @Override origin origin() {
    return this.the_origin;
  }
  public @Override @Nullable readonly_list<notification> secondary() {
    return this.the_secondary;
  }
  public @Override log_level level() {
    return log_level.ERROR;
  }
  public @Override string to_string() {
    return this.the_message;
  }
  public @Override text_fragment to_text() {
    return this.render_text(true);
  }
  public @Override void report() {
    notification_context.get().write(this);
    return;
  }
  public @Override text_fragment render_text(final boolean prefix_with_source) {
    string full_message;
    if (prefix_with_source) {
      full_message = ideal.machine.elements.runtime_util.concatenate(origin_utilities.get_source_prefix(this.the_origin), this.the_message);
    } else {
      full_message = this.the_message;
    }
    final string MESSAGE_CLASS = new base_string("message");
    final text_fragment primary = text_utilities.join(origin_printer.show_origin(this.the_origin), new base_element(text_library.DIV, text_library.CLASS, (base_string) MESSAGE_CLASS, (base_string) full_message));
    if (this.the_secondary != null) {
      text_fragment secondaries_text = text_utilities.EMPTY_FRAGMENT;
      {
        final readonly_list<notification> secondary_element_list = this.the_secondary;
        for (Integer secondary_element_index = 0; secondary_element_index < secondary_element_list.size(); secondary_element_index += 1) {
          final notification secondary_element = secondary_element_list.get(secondary_element_index);
          secondaries_text = text_utilities.join(secondaries_text, secondary_element.render_text(prefix_with_source));
        }
      }
      return text_utilities.join(primary, new base_element(text_library.INDENT, secondaries_text));
    } else {
      return primary;
    }
  }
}
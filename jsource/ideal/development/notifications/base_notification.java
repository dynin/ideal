/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;

public class base_notification implements notification {

  private string message;
  private origin the_origin;
  private @Nullable readonly_list<notification> secondary;

  public base_notification(string message, origin the_origin,
      @Nullable readonly_list<notification> secondary) {
    assert message != null;
    assert the_origin != null;
    this.message = message;
    this.the_origin = the_origin;
    this.secondary = (secondary != null && secondary.is_not_empty()) ? secondary : null;
  }

  public base_notification(string message, origin the_origin) {
    this(message, the_origin, null);
  }

  public base_notification(String message, origin the_origin) {
    this(new base_string(message), the_origin, null);
  }

  @Override
  public string message() {
    return message;
  }

  @Override
  public origin origin() {
    return the_origin;
  }

  @Override
  public @Nullable readonly_list<notification> secondary() {
    return secondary;
  }

  @Override
  public log_level level() {
    // TODO: support warnings, etc.
    return log_level.ERROR;
  }

  @Override
  public string to_string() {
    return message;
  }

  @Override
  public text_fragment to_text() {
    return render_text(true);
  }

  @Override
  public void report() {
    notification_context.get().write(this);
  }

  @Override
  public text_fragment render_text(boolean prefix_with_source) {
    base_string full_message;
    if (prefix_with_source) {
      full_message = new base_string(position_util.get_source_prefix(the_origin), message);
    } else {
      full_message = (base_string) message;
    }
    // TODO: do not hardcode style.
    text_fragment primary = text_util.join(position_printer.show_origin(the_origin),
        base_element.make(text_library.DIV, text_library.CLASS, new base_string("message"),
            full_message));

    if (secondary != null) {
      text_fragment secondaries = text_util.EMPTY_FRAGMENT;
      for (int i = 0; i < secondary.size(); ++i) {
        secondaries = text_util.join(secondaries, secondary.get(i).render_text(prefix_with_source));
      }
      return text_util.join(primary, base_element.make(text_library.INDENT, secondaries));
    } else {
      return primary;
    }
  }
}

/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.types.*;

public class notification_util {

  public static readonly_list<notification> to_notifications(readonly_list<action> sources,
      value_printer printer) {
    list<notification> notifications = new base_list<notification>();
    for (int i = 0; i < sources.size(); ++i) {
      declaration the_declaration = declaration_util.get_declaration(sources.get(i));
      assert the_declaration != null;
      string result_type = printer.print_value(sources.get(i).result());
      notification the_notification = new base_notification(
          new base_string("(declaration #" + i + ": " + result_type + ")"), the_declaration);
      notifications.append(the_notification);
    }
    // TODO: fix this by introducing a stable order
    return notifications;
  }

  private notification_util() { }
}

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

public class notification_context {

  private static output<notification> logger;

  public static output<notification> get() {
    assert logger != null;
    return logger;
  }

  public static void set(output<notification> new_logger) {
    logger = new_logger;
  }
}

/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class panic_value extends jump_wrapper {

  public final string message;

  public panic_value(string message) {
    this.message = message;
  }

  @Override
  public string to_string() {
    return new base_string("panic value: ", message);
  }
}

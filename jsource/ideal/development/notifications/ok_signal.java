/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.origins.*;
import ideal.development.types.*;
import ideal.development.actions.*;
import javax.annotation.Nullable;

public class ok_signal extends debuggable implements signal {

  public static final ok_signal instance = new ok_signal();

  static final origin no_origin = new special_origin(new base_string("no-origin"));

  @Override
  public origin deeper_origin() {
    return no_origin;
  }

  @Override
  public string to_string() {
    return new base_string("ok");
  }
}

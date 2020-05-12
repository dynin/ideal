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
import ideal.development.elements.*;

public class fragment_position extends debuggable implements deeply_immutable_data, origin {
  public final origin begin;
  public final origin main;
  public final origin end;

  public fragment_position(origin begin, origin main, origin end) {
    this.begin = begin;
    this.main = main;
    this.end = end;
    assert begin != null;
    assert main != null;
    assert end != null;
  }

  public origin deeper_origin() {
    return main;
  }
}

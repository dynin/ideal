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

public class fragment_position extends debuggable implements deeply_immutable_data, position {
  public final position begin;
  public final position main;
  public final position end;

  public fragment_position(position begin, position main, position end) {
    this.begin = begin;
    this.main = main;
    this.end = end;
    assert begin != null;
    assert main != null;
    assert end != null;
  }

  public position source_position() {
    return main;
  }
}

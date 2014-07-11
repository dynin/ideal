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

// TODO: zone should be part of the execution_context...
public class dummy_zone implements zone_wrapper {
  public static final dummy_zone instance = new dummy_zone();

  private dummy_zone() { }
  @Override
  public void mark_modified() { }
}

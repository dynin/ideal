/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class enum_util {
  public static boolean can_be_enum_value(construct the_construct) {
    return the_construct instanceof name_construct || the_construct instanceof parameter_construct;
  }
}

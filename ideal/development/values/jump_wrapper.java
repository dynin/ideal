/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.core_types;

public abstract class jump_wrapper implements value_wrapper {

  @Override
  public type type_bound() {
    return core_types.unreachable_type();
  }

  @Override
  public readonly_value unwrap() {
    return core_types.unreachable_type();
  }
}

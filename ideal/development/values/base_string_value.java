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
import ideal.runtime.reflections.*;
import ideal.development.elements.*;

public class base_string_value extends base_constant_value<string> implements string_value {

  public base_string_value(string value, type bound) {
    super(value, bound);
  }

  @Override
  public string constant_to_string() {
    return unwrap();
  }
}

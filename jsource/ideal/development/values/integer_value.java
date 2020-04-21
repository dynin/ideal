/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class integer_value extends base_constant_value<Integer> {

  public integer_value(int value, type bound) {
    super(/*new base_integer(value)*/value, bound);
  }

  @Override
  public string constant_to_string() {
    return new base_string(unwrap().toString());
  }
}

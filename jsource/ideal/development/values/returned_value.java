/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.jumps.*;

public class returned_value extends jump_wrapper {
  public entity_wrapper result;

  public returned_value(entity_wrapper result) {
    this.result = result;
  }

  @Override
  public string to_string() {
    return new base_string(new base_string("return value: "), result.to_string());
  }
}

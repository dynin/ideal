/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.functions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class multiply_op extends base_number_op {
  public multiply_op() {
    super(operator.MULTIPLY, library().immutable_integer_type());
  }

  @Override
  protected Integer apply(int first, int second) {
    return first * second;
  }
}

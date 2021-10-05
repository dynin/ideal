/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.types.*;
import ideal.development.analyzers.*;

public class add_op extends base_number_op {
  public add_op() {
    super(operator.ADD, common_types.immutable_integer_type());
  }

  @Override
  protected Integer apply(int first, int second) {
    return first + second;
  }

  @Override
  public declaration get_declaration() {
    return builtin_declaration.instance;
  }
}

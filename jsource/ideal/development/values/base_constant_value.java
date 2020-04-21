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
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public abstract class base_constant_value<T /*extends deeply_immutable_data*/>
    extends base_data_value<T> {

  private T boxed;

  public base_constant_value(T boxed, type bound) {
    super(bound);
    assert boxed != null;
    this.boxed = boxed;
  }

  @Override
  public T unwrap() {
    return boxed;
  }

  public abstract string constant_to_string();

  @Override
  public string to_string() {
    return utilities.describe(this, constant_to_string());
  }
}

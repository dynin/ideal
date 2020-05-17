/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.development.declarations.*;
import ideal.development.names.*;

import javax.annotation.Nullable;

public class list_value extends debuggable
    implements value_wrapper<any_list<value_wrapper>>, stringable {

  any_list<value_wrapper> list_value;
  private type bound;

  public list_value(any_list<value_wrapper> list_value, type bound) {
    this.list_value = list_value;
    this.bound = bound;
  }

  @Override
  public type type_bound() {
    return bound;
  }

  @Override
  public any_list<value_wrapper> unwrap() {
    return list_value;
  }
}

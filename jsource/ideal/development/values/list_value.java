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
import ideal.development.declarations.*;
import ideal.development.names.*;
import ideal.development.types.*;

import javax.annotation.Nullable;

public class list_value extends debuggable implements composite_wrapper<any_list<value_wrapper>> {

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

  @Override
  public value_wrapper get_var(variable_id key) {
    if (key.short_name() == common_names.size_name) {
      return new integer_value(((readonly_list) list_value).size(),
          common_types.immutable_nonnegative_type());
    }

    utilities.panic("Failing list_value.get_var() for " + key);
    return null;
  }

  @Override
  public void put_var(variable_id key, value_wrapper value) {
    utilities.panic("Failing list_value.put_var() for " + key);
  }
}

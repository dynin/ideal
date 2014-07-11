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
import ideal.development.declarations.*;

public class base_composite_value extends debuggable implements composite_wrapper,
    any_composite_value {

  private final type bound;
  private final zone_wrapper the_zone;
  private final dictionary<variable_id, value_wrapper> bindings;

  public base_composite_value(type bound, zone_wrapper the_zone) {
    this.bound = bound;
    this.the_zone = the_zone;
    bindings = new hash_dictionary<variable_id, value_wrapper>();
  }

  @Override
  public type type_bound() {
    return bound;
  }

  @Override
  public any_composite_value unwrap() {
    return this;
  }

  @Override
  public void put_var(variable_id key, value_wrapper value) {
    bindings.put(key, value);
  }

  @Override
  public value_wrapper get_var(variable_id key) {
    return bindings.get(key);
  }

  public string to_string() {
    return new base_string("(composite)" + type_bound().to_string(), ":{",
        bindings.toString(), "}");
  }
}

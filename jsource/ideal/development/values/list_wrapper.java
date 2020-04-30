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
import ideal.development.names.*;

public class list_wrapper extends debuggable implements value_wrapper<list<value_wrapper>> {

  private final list<value_wrapper> wrapped;
  private final type bound;
  private final zone_wrapper the_zone;

  public list_wrapper(list<value_wrapper> wrapped, type bound, zone_wrapper the_zone) {
    this.wrapped = wrapped;
    this.bound = bound;
    this.the_zone = the_zone;
    assert wrapped != null && bound != null && the_zone != null;
  }

  @Override
  public list<value_wrapper> unwrap() {
    return wrapped;
  }

  @Override
  public type type_bound() {
    return bound;
  }

  public zone_wrapper zone() {
    return the_zone;
  }

  @Override
  public string to_string() {
    return new base_string(bound.toString(), "[", wrapped.toString(), "]");
  }
}

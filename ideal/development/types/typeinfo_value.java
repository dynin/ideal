/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.flavors.*;

public class typeinfo_value extends debuggable implements value_wrapper {

  private final type the_type;

  public typeinfo_value(type the_type) {
    this.the_type = the_type;
  }

  public type get_type() {
    return the_type;
  }

  // TODO: shouldn't we return a metatype here?
  @Override
  public type type_bound() {
    return the_type;
  }

  @Override
  public value unwrap() {
    return the_type;
  }

  @Override
  public string to_string() {
    return new base_string("typeinfo-value: ", the_type.to_string());
  }
}

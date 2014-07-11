/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.flavors.*;

public class allocate_action extends base_action {
  private final principal_type the_type;

  public allocate_action(principal_type the_type, position source) {
    super(source);
    this.the_type = the_type;
  }

  @Override
  public abstract_value result() {
    return the_type.get_flavored(flavors.raw_flavor);
  }

  // TODO: instead of dummy_zone, use a zone derived from the execution_context
  @Override
  public value_wrapper execute(execution_context context) {
    return new base_composite_value(the_type.get_flavored(flavors.mutable_flavor),
        dummy_zone.instance);
  }

  @Override
  public string to_string() {
    return new base_string("allocate: ", the_type.to_string());
  }
}

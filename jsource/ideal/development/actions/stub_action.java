/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.origins.*;

import javax.annotation.Nullable;

public class stub_action extends base_action {
  private final type the_type;

  public stub_action(type the_type) {
    super(origin_utilities.no_origin);
    this.the_type = the_type;
  }

  @Override
  public abstract_value result() {
    return the_type;
  }

  @Override
  public boolean has_side_effects() {
    return false;
  }

  @Override
  public final action combine(action from, origin the_origin) {
    return from;
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    return from_entity;
  }

  @Override
  public @Nullable declaration get_declaration() {
    return the_type.principal().get_declaration();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_type);
  }
}

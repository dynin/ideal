/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.declarations.*;
import ideal.development.jumps.*;

/**
 * Dereference a reference entity.  Implements calling ref.get().
 */
public class dereference_action extends base_action implements chainable_action {

  private final type value_type;
  @Nullable private final declaration the_declaration;

  public dereference_action(type value_type, @Nullable declaration the_declaration,
      origin the_origin) {
    super(the_origin);
    this.value_type = value_type;
    this.the_declaration = the_declaration;
  }

  @Override
  public abstract_value result() {
    return value_type;
  }

  @Override
  public boolean has_side_effects() {
    return false;
  }

  @Override
  public final action combine(action from, origin the_origin) {
    return new chain_action(from, this, the_origin);
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    // Handle jumps
    if (from_entity instanceof reference_wrapper) {
      return ((reference_wrapper) from_entity).get();
    } else {
      return new panic_value(new base_string("Reference not found"));
    }
  }

  @Override
  public @Nullable declaration get_declaration() {
    return the_declaration;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, value_type);
  }
}

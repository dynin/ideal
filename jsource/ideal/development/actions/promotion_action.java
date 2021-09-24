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

public class promotion_action extends base_action implements chainable_action {
  public final type the_type;
  public final boolean is_supertype;

  public promotion_action(type the_type, boolean is_supertype, origin the_origin) {
    super(the_origin);
    this.the_type = the_type;
    this.is_supertype = is_supertype;
  }

  public promotion_action(type the_type, boolean is_supertype) {
    this(the_type, is_supertype, origin_utilities.no_origin);
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
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    entity_wrapper result = from_entity;

    if (the_type == library().immutable_void_type()) {
      result = library().void_instance();
    }

    if (result instanceof reference_wrapper && !library().is_reference_type(the_type)) {
      result = ((reference_wrapper) result).get();
      // TODO: verify that type matches
    }

    return result;
  }

  @Override
  public @Nullable declaration get_declaration() {
  /*
    if (is_supertype) {
      return the_type.principal().get_declaration();
    }*/

    return null;
  }

  private static common_library library() {
    return common_library.get_instance();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_type);
  }
}

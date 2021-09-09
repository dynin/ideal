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

public class promotion_action extends base_action {
  private final type the_type;
  private final @Nullable action the_action;
  public final boolean is_supertype;

  private promotion_action(type the_type, @Nullable action the_action, boolean is_supertype,
      origin source) {
    super(source);
    this.the_type = the_type;
    this.the_action = the_action;
    this.is_supertype = is_supertype;
  }

  public promotion_action(type the_type, boolean is_supertype, origin source) {
    this(the_type, null, is_supertype, source);
  }

  public promotion_action(type the_type, boolean is_supertype) {
    this(the_type, is_supertype, origin_utilities.no_origin);
  }

  public @Nullable action get_action() {
    return the_action;
  }

  @Override
  public abstract_value result() {
    return the_type;
  }

  @Override
  public boolean has_side_effects() {
    return the_action != null && the_action.has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    entity_wrapper result;

    if (!(from_entity instanceof null_wrapper)) {
      result = from_entity;
    } else {
      assert the_action != null;
      result = the_action.execute(null_wrapper.instance, context);
    }

    if (the_type == library().immutable_void_type()) {
      result = library().void_instance();
    }

    if (result instanceof reference_wrapper &&
        !library().is_reference_type(the_type)) {
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

    if (the_action != null) {
      return the_action.get_declaration();
    } else {
      return the_type.principal().get_declaration();
      //return null;
    }
  }

  @Override
  public action bind_from(action from, origin source) {
    if (from.result() == the_type) {
      return from;
    }

    while (from instanceof promotion_action) {
      promotion_action action_from = (promotion_action) from;
      //if (action_from.is_supertype != this.is_supertype) {
      //  break;
      //}
      from = action_from.get_action();
      if (from == null) {
        return this;
      }
    }

    if (the_action != null) { // && !is_supertype) {
      from = the_action.bind_from(from, source);
    }

    // TODO: verify that from.result() is a subtype of the_type
    // TODO: collapse chained promotion_actions
    return new promotion_action(the_type, from, is_supertype, source);
  }

  private static common_library library() {
    return common_library.get_instance();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_type);
  }
}

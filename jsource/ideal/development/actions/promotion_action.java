/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

import javax.annotation.Nullable;

public class promotion_action extends base_action {
  private final type the_type;
  private final @Nullable action the_action;

  private promotion_action(type the_type, @Nullable action the_action, origin source) {
    super(source);
    this.the_type = the_type;
    this.the_action = the_action;
  }

  public promotion_action(type the_type, origin source) {
    this(the_type, null, source);
  }

  public promotion_action(type the_type) {
    this(the_type, action_utilities.no_origin);
  }

  public @Nullable action get_action() {
    return the_action;
  }

  @Override
  public abstract_value result() {
    return the_type;
  }

  @Override
  public entity_wrapper execute(execution_context context) {
    assert the_action != null;
    return the_action.execute(context);
  }

  @Override
  public @Nullable declaration get_declaration() {
    if (the_action != null) {
      return the_action.get_declaration();
    } else {
      return null;
    }
  }

  @Override
  public action bind_from(action from, origin source) {
    if (from.result() == the_type) {
      return from;
    }

    while (from instanceof promotion_action) {
      from = ((promotion_action) from).get_action();
      if (from == null) {
        return this;
      }
    }

    if (the_action != null) {
      from = the_action.bind_from(from, source);
    }

    // TODO: verify that from.result() is a subtype of the_type
    // TODO: collapse chained promotion_actions
    return new promotion_action(the_type, from, source);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_type);
  }
}

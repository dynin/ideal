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
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.declarations.*;

/**
 * Dereference a reference entity.  Implements calling ref.get().
 */
public class dereference_action extends base_action {

  private final type value_type;
  @Nullable
  private final declaration the_declaration;
  @Nullable
  public final action from;

  private dereference_action(@Nullable action from, type value_type,
      @Nullable declaration the_declaration, origin source) {
    super(source);
    this.value_type = value_type;
    this.the_declaration = the_declaration;
    this.from = from;
  }

  public dereference_action(type value_type, @Nullable declaration the_declaration,
      origin source) {
    this(null, value_type, the_declaration, source);
  }

  @Override
  public abstract_value result() {
    return value_type;
  }

  @Override
  public boolean has_side_effects() {
    return from != null && from.has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    // Handle jumps
    if (from_entity instanceof reference_wrapper) {
      return ((reference_wrapper) from_entity).get();
    }

    if (from == null) {
      utilities.panic("Unbound " + value_type);
    }
    return ((reference_wrapper) from.execute(null_wrapper.instance, context)).get();
  }

  @Override
  public @Nullable declaration get_declaration() {
    if (the_declaration != null) {
      return the_declaration;
    } else {
      return declaration_util.get_declaration(from);
    }
  }

  @Override
  public action bind_from(action new_from, origin pos) {
    if (from != null) {
      assert !(from instanceof type_action);
      new_from = from.bind_from(new_from, pos);
    }

    return new dereference_action(new_from, value_type, the_declaration, pos);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, from);
  }
}

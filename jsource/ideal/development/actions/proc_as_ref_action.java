/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
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
import ideal.development.notifications.*;
import ideal.development.flavors.*;

/**
 * Reference a proc_as_ref.
 */
public class proc_as_ref_action extends base_action implements chainable_action {

  public final procedure_declaration the_declaration;

  public proc_as_ref_action(procedure_declaration the_declaration) {
    super(the_declaration);
    this.the_declaration = the_declaration;
  }

  private type get_return_type() {
    return the_declaration.get_return_type();
  }

  public type type_bound() {
    return common_types.get_reference(flavor.readonly_flavor, get_return_type());
  }

  @Override
  public abstract_value result() {
    return type_bound();
  }

  @Override
  public boolean has_side_effects() {
    return !the_declaration.is_pure();
  }

  @Override
  public final action combine(action from, origin the_origin) {
    return new chain_action(from, this, the_origin);
  }

  @Override
  public reference_wrapper execute(entity_wrapper from_entity, execution_context the_context) {
    // TODO: handle jumps
    value_wrapper this_argument = (value_wrapper) from_entity;
    entity_wrapper result = action_utilities.execute_procedure(the_declaration,
        this_argument, new empty<entity_wrapper>(), the_context);
    // TODO: fix.
    if (false) {
      if (!action_utilities.is_of(result, get_return_type())) {
        log.debug("result: " + result.type_bound() + " expected " + get_return_type());
        log.debug("proc: " + the_declaration);
      }
      assert action_utilities.is_of(result, get_return_type());
    }

    type ref_type = common_types.get_reference(flavor.readonly_flavor,
        action_utilities.to_type(result.type_bound()));
    return new constant_reference((value_wrapper) result, ref_type);
  }

  @Override
  public declaration get_declaration() {
    return the_declaration;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_declaration.short_name());
  }
}

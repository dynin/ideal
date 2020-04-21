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
public class proc_as_ref_action extends base_action {

  public final procedure_declaration the_declaration;
  @Nullable
  public final action from;

  private proc_as_ref_action(procedure_declaration the_declaration, @Nullable action from,
      position source) {
    super(source);
    this.the_declaration = the_declaration;
    this.from = from;
  }

  public proc_as_ref_action(procedure_declaration the_declaration) {
    this(the_declaration, null, the_declaration);
  }

  private type get_return_type() {
    return the_declaration.get_return_type();
  }

  public type type_bound() {
    return common_library.get_instance().get_reference(flavors.readonly_flavor, get_return_type());
  }

  @Override
  public abstract_value result() {
    return type_bound();
  }

  @Override
  public reference_wrapper execute(execution_context the_context) {
    if (from == null) {
      utilities.panic("Unbound proc_as_ref " + the_declaration.short_name());
    }
    value_wrapper this_argument = (value_wrapper) from.execute(the_context);

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

    type ref_type = common_library.get_instance().get_reference(flavors.readonly_flavor,
        action_utilities.to_type(result.type_bound()));
    return new constant_reference<any_value>((value_wrapper) result, ref_type);
  }

  @Override
  public declaration get_declaration() {
    return the_declaration;
  }

  @Override
  public action bind_from(action new_from, position source) {
    if (from != null) {
      new_from = from.bind_from(new_from, source);
    }
    return new proc_as_ref_action(the_declaration, new_from, source);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_declaration.short_name());
  }
}

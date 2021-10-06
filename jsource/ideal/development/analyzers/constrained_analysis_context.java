/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.machine.channels.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.flavors.*;
import ideal.development.actions.*;
import ideal.development.notifications.*;

public class constrained_analysis_context extends debuggable implements analysis_context {

  public final base_analysis_context parent;
  public final constraint_state state;

  public constrained_analysis_context(base_analysis_context parent, constraint_state state) {
    this.parent = parent;
    this.state = state;
  }

  @Override
  public language_settings settings() {
    return parent.settings();
  }

  @Override
  public readonly_list<action> lookup(type from, action_name name) {
    return parent.lookup(from, name);
  }

  @Override
  public void add(type from, action_name name, action the_action) {
    parent.add(from, name, the_action);
  }

  @Override
  public void add_supertype(type subtype, type supertype) {
    parent.add_supertype(subtype, supertype);
  }

  @Override
  public readonly_list<action> resolve(type from, action_name name, origin pos) {
    return parent.resolve(from, name, pos);
  }

  @Override
  public string print_value(abstract_value the_value) {
    return parent.print_value(the_value);
  }

  @Override
  public boolean is_subtype_of(abstract_value the_value, type the_type) {
    return parent.is_subtype_of(the_value, the_type);
  }

  @Override
  public type find_supertype_procedure(abstract_value the_value) {
    return parent.find_supertype_procedure(the_value);
  }

  @Override
  public boolean can_promote(action from, type target) {
    return parent.find_promotion(from, target, state) != null;
  }

  @Override
  public action promote(action from, type target, origin pos) {
    // TODO: share the code with base_analysis_context
    if (from instanceof error_signal) {
      return from;
    }

    @Nullable action result = parent.find_promotion(from, target, state);

    // TODO: unify code with base_analysis_context
    if (result != null) {
      return action_utilities.combine(from, result, pos);
    } else {
      error_signal signal = action_utilities.cant_promote(from.result(), target, this, pos);
      //return new error_action(signal);
      utilities.panic(signal.to_string());
      return null;
    }
  }

  @Override
  public action to_value(action expression, origin the_origin) {
    // We need to specially handled narrowed variables here
    // because the reference type is not narrowed.
    // Say the variable declaration is "string or null foo", and it's narrowed to string.
    // The is_reference_type(the_type) would return a union type, which is not what we want.
    action narrowed_action = parent.can_narrow(expression, state);
    if (narrowed_action != null) {
      return narrowed_action;
    }

    type the_type = expression.result().type_bound();
    if (common_types.is_reference_type(the_type)) {
      // TODO: check that flavor is readonly or mutable.
      type value_type = common_types.get_reference_parameter(the_type);
      // TODO: replace this with a promotion lookup.
      return promote(expression, value_type, the_origin);
    } else {
      return expression;
    }
  }

  @Override
  public boolean is_parametrizable(type the_type) {
    return parent.is_parametrizable(the_type);
  }

  @Override
  public graph<principal_type, origin> type_graph() {
    return parent.type_graph();
  }

  @Override
  public void declare_type(principal_type new_type, declaration_pass pass) {
    parent.declare_type(new_type, pass);
  }

  @Override
  public master_type get_or_create_type(action_name name, kind kind, principal_type parent,
      flavor_profile the_flavor_profile) {
    utilities.panic("get_or_create_type() not supported");
    return null;
  }

  public constraint_state constraints() {
    return state;
  }

  @Override
  public string to_string() {
    string_writer content = new string_writer();
    content.write_all(new base_string("context "));
    content.write_all(parent.to_string());
    content.write_all(new base_string(" "));
    content.write_all(state.to_string());
    return content.elements();
  }
}

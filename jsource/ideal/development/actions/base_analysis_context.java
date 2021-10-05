/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import ideal.runtime.elements.*;
import ideal.runtime.graphs.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.flavors.*;
import ideal.development.origins.*;
import ideal.development.kinds.*;
import ideal.development.notifications.*;

public abstract class base_analysis_context extends debuggable implements analysis_context {

  private static action_table actions = new action_table();

  private final base_semantics language;
  private graph<principal_type, origin> the_type_graph;

  protected base_analysis_context(base_semantics language) {
    this.language = language;
    this.the_type_graph = new base_graph<principal_type, origin>();
    if (!common_types.is_initialized()) {
      new common_types(this);
    }
    if (!common_values.is_initialized()) {
      common_values.initialize();
    }
  }

  @Override
  public semantics language() {
    return language;
  }

  @Override
  public readonly_list<action> lookup(type from, action_name name) {
    return actions.lookup(from, name);
  }

  @Override
  public void add(type from, action_name name, action the_action) {
    actions.add(from, name, the_action);
  }

  @Override
  public void add_supertype(type subtype, type supertype) {
    actions.add(subtype, special_name.SUPERTYPE, supertype.to_action(origin_utilities.no_origin));
  }

  @Override
  public readonly_list<action> resolve(type from, action_name name, origin pos) {
    return language.resolve(actions, from, name, pos);
  }

  @Override
  public string print_value(abstract_value the_value) {
    return language.print_value(the_value);
  }

  @Override
  public boolean is_subtype_of(abstract_value the_value, type the_type) {
    return language.is_subtype_of(actions, the_value, the_type);
  }

  @Override
  public type find_supertype_procedure(abstract_value the_value) {
    return language.find_supertype_procedure(actions, the_value);
  }

  public @Nullable action find_promotion(action from, type target,
      @Nullable function1<abstract_value, variable_declaration> constraint_mapper) {

    if (constraint_mapper != null) {
      @Nullable narrow_action the_action = can_narrow(from, constraint_mapper);
      if (the_action != null) {
        @Nullable action result = find_promotion(the_action, target, null);
        if (result != null) {
          return action_utilities.combine(the_action, result, origin_utilities.no_origin);
        }
      }
    }

    return language.find_promotion(actions, from.result().type_bound(), target);
  }

  public @Nullable narrow_action can_narrow(action from,
      @Nullable function1<abstract_value, variable_declaration> constraint_mapper) {

    @Nullable declaration the_declaration = declaration_util.get_declaration(from);
    if (the_declaration != null && the_declaration instanceof variable_declaration) {
      variable_declaration the_variable_declaration = (variable_declaration) the_declaration;
      @Nullable abstract_value narrowed = constraint_mapper.call(the_variable_declaration);
      if (narrowed != null) {
        origin the_origin = from;
        type narrowed_type = narrowed.type_bound();
        if (from instanceof narrow_action) {
          return new narrow_action((((narrow_action) from).expression), narrowed_type,
              the_variable_declaration, the_origin);
        } else {
          return new narrow_action(from, narrowed_type,
              the_variable_declaration, the_origin);
        }
      }
    }

    return null;
  }

  @Override
  public action to_value(action expression, origin the_origin) {
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
  public boolean can_promote(action from, type target) {
    return find_promotion(from, target, null) != null;
  }

  @Override
  public action promote(action from, type target, origin pos) {
    if (from instanceof error_signal) {
      return from;
    }

    @Nullable action result = find_promotion(from, target, null);

    // TODO: unify code with constrained_analysis_context
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
  public graph<principal_type, origin> type_graph() {
    return the_type_graph;
  }

  @Override
  public boolean is_parametrizable(type the_type) {
    if (the_type instanceof master_type) {
      return ((master_type) the_type).has_parametrizable_state();
    }

    return action_utilities.is_procedure_type(the_type);
  }

  @Override
  public void declare_type(principal_type new_type, declaration_pass pass) {
    language.declare_type(new_type, pass, this);
  }

  @Override
  public master_type get_or_create_type(action_name name, kind kind, principal_type parent,
      flavor_profile the_flavor_profile) {
    readonly_list<type> types = action_utilities.lookup_types(this, parent, name);
    // TODO: handle error conditions
    if (types.size() == 1) {
      return (master_type) types.first();
    } else {
      return action_utilities.make_type(this, kind, the_flavor_profile, name, parent,
          null, semantics.BUILTIN_POSITION);
    }
  }
}

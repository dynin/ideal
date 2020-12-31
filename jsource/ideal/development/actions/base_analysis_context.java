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
import ideal.development.notifications.*;

public abstract class base_analysis_context extends debuggable implements analysis_context {

  private static action_table actions = new action_table();

  private immutable_dictionary<declaration, abstract_value> empty_constraints =
      new list_dictionary<declaration, abstract_value>().frozen_copy();

  private final base_semantics language;
  private final dictionary<construct, analyzable> eval_bindings;
  private graph<principal_type, origin> the_type_graph;

  protected base_analysis_context(base_semantics language) {
    this.language = language;
    this.eval_bindings = new hash_dictionary<construct, analyzable>();
    this.the_type_graph = new base_graph<principal_type, origin>();
    if (!common_library.is_initialized()) {
      new common_library(this);
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
      @Nullable immutable_dictionary<declaration, abstract_value> constraint_bindings) {

    if (constraint_bindings != null) {
      @Nullable declaration the_declaration = declaration_util.get_declaration(from);
      if (the_declaration != null) {
        @Nullable abstract_value narrowed = constraint_bindings.get(the_declaration);
        if (narrowed != null) {
          origin the_origin = from;
          variable_declaration the_variable_declaration = (variable_declaration) the_declaration;
          type narrowed_type = narrowed.type_bound();
          narrow_action the_action = new narrow_action(from, narrowed_type,
              the_variable_declaration, the_origin);
          @Nullable action result = find_promotion(the_action, target, null);
          if (result != null) {
            return result;
          }
        }
      }
    }

    return language.find_promotion(actions, from, target);
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

    if (result != null) {
      return result.bind_from(from, pos);
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
  public void declare_type(principal_type new_type, declaration_pass pass) {
    language.declare_type(new_type, pass, this);
  }

  @Override
  public @Nullable analyzable get_analyzable(construct c) {
    return eval_bindings.get(c);
  }

  @Override
  public void put_analyzable(construct c, analyzable a) {
    // TODO: overwriting an existing construct shouldn't happen,
    // but maybe fail gracefully here?
    assert eval_bindings.get(c) == null;
    eval_bindings.put(c, a);
  }

  @Override
  public immutable_dictionary<declaration, abstract_value> constraints() {
    return empty_constraints;
  }
}

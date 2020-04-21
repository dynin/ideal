/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

  private final base_semantics language;
  private final dictionary<construct, analyzable> eval_bindings;
  private graph<principal_type, position> the_type_graph;

  protected base_analysis_context(base_semantics language) {
    this.language = language;
    this.eval_bindings = new hash_dictionary<construct, analyzable>();
    this.the_type_graph = new base_graph<principal_type, position>();
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
  public readonly_list<action> resolve(type from, action_name name, @Nullable action_target target,
      position pos) {
    return language.resolve(actions, from, name, target, pos);
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
  public type find_supertype(abstract_value the_value, action_target target) {
    return language.find_supertype(actions, the_value, target);
  }

  private @Nullable action find_promotion(abstract_value from, type target) {
    return language.find_promotion(actions, from, new specific_type_target(target));
  }

  @Override
  public boolean can_promote(abstract_value from, type target) {
    return find_promotion(from, target) != null;
  }

  @Override
  public action promote(action from, type target, position pos) {
    if (from instanceof error_signal) {
      return from;
    }

    @Nullable action result = find_promotion(from.result(), target);

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
  public graph<principal_type, position> type_graph() {
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
    eval_bindings.put(c, a);
  }

  @Override
  public @Nullable abstract_value lookup_constraint(declaration the_declaration) {
    return null;
  }
}

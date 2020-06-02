/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

  private final analysis_context parent;
  private final immutable_dictionary<declaration, abstract_value> constraint_bindings;

  public constrained_analysis_context(analysis_context parent,
      immutable_dictionary<declaration, abstract_value> constraint_bindings) {
    this.parent = parent;
    this.constraint_bindings = constraint_bindings;
  }

  public static analysis_context combine(analysis_context parent,
      readonly_list<constraint> the_constraints) {
    if (the_constraints.is_empty()) {
      return parent;
    }

    dictionary<declaration, abstract_value> constraint_dictionary =
        new list_dictionary<declaration, abstract_value>();
    for (int i = 0; i < the_constraints.size(); ++i) {
      constraint the_constraint = the_constraints.get(i);
      // TODO: check that constraint isn't is trivial
      //  and is either part of the declaration or part of the context.
      constraint_dictionary.put(the_constraint.the_declaration, the_constraint.the_value);
    }

    assert constraint_dictionary.is_not_empty();
    return new constrained_analysis_context(parent, constraint_dictionary.frozen_copy());
  }

  @Override
  public semantics language() {
    return parent.language();
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
  public boolean can_promote(abstract_value from, type target) {
    return parent.can_promote(from, target);
  }

  @Override
  public action promote(action from, type target, origin pos) {
    return parent.promote(from, target, pos);
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
  public @Nullable analyzable get_analyzable(construct c) {
    return parent.get_analyzable(c);
  }

  @Override
  public void put_analyzable(construct c, analyzable a) {
    parent.put_analyzable(c, a);
  }

  @Override
  public @Nullable readonly_list<construct> load_type_body(
      type_announcement_construct the_announcement) {
    return parent.load_type_body(the_announcement);
  }

  @Override
  public @Nullable abstract_value lookup_constraint(declaration the_declaration) {
    @Nullable abstract_value result = constraint_bindings.get(the_declaration);
    if (result != null) {
      return result;
    } else {
      return parent.lookup_constraint(the_declaration);
    }
  }
}

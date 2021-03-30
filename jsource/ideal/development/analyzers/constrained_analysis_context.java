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

  private final base_analysis_context parent;
  private final immutable_dictionary<declaration, abstract_value> constraint_bindings;

  public constrained_analysis_context(base_analysis_context parent,
      immutable_dictionary<declaration, abstract_value> constraint_bindings) {
    this.parent = parent;
    this.constraint_bindings = constraint_bindings;
  }

  public static analysis_context combine(analysis_context parent,
      readonly_list<constraint> the_constraints) {
    if (the_constraints.is_empty()) {
      return parent;
    }

    base_analysis_context new_parent;
    if (parent instanceof base_analysis_context) {
      new_parent = (base_analysis_context) parent;
    } else {
      new_parent = ((constrained_analysis_context) parent).parent;
    }

    dictionary<declaration, abstract_value> constraint_dictionary =
        new list_dictionary<declaration, abstract_value>();

    // TODO: implement dictionary.copy() or dictionary.add_all()
    readonly_list<dictionary.entry<declaration, abstract_value>> parent_constraints =
        parent.constraints().elements();
    for (int i = 0; i < parent_constraints.size(); ++i) {
      dictionary.entry<declaration, abstract_value> the_constraint = parent_constraints.get(i);
      constraint_dictionary.put(the_constraint.key(), the_constraint.value());
    }

    for (int i = 0; i < the_constraints.size(); ++i) {
      constraint the_constraint = the_constraints.get(i);
      // TODO: check that constraint isn't trivial
      //  and is either part of the declaration or part of the context.
      constraint_dictionary.put(the_constraint.the_declaration, the_constraint.the_value);
    }

    assert constraint_dictionary.is_not_empty();
    return new constrained_analysis_context(new_parent, constraint_dictionary.frozen_copy());
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
  public boolean can_promote(action from, type target) {
    return parent.find_promotion(from, target, constraint_bindings) != null;
  }

  @Override
  public action promote(action from, type target, origin pos) {
    // TODO: share the code with base_analysis_context
    if (from instanceof error_signal) {
      return from;
    }

    @Nullable action result = parent.find_promotion(from, target, constraint_bindings);

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
    return parent.type_graph();
  }

  @Override
  public void declare_type(principal_type new_type, declaration_pass pass) {
    parent.declare_type(new_type, pass);
  }

  @Override
  public @Nullable readonly_list<construct> load_resource(
      type_announcement_construct the_announcement) {
    return parent.load_resource(the_announcement);
  }

  @Override
  public immutable_dictionary<declaration, abstract_value> constraints() {
    return constraint_bindings;
  }

  @Override
  public string to_string() {
    string_writer content = new string_writer();
    content.write_all(new base_string("context {"));
    readonly_list<dictionary.entry<declaration, abstract_value>> constraints =
        constraints().elements();
    for (int i = 0; i < constraints.size(); ++i) {
      dictionary.entry<declaration, abstract_value> the_constraint = constraints.get(i);
      content.write_all(the_constraint.key().to_string());
      content.write_all(new base_string(": "));
      content.write_all(the_constraint.value().to_string());
      content.write_all(new base_string(", "));
    }
    content.write_all(new base_string("}"));
    return content.elements();
  }
}

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
  private final immutable_dictionary<variable_declaration, constraint> constraint_bindings;
  private function1<abstract_value, variable_declaration> constraint_mapper =
      new function1<abstract_value, variable_declaration>() {
          @Override public abstract_value call(variable_declaration the_declaration) {
            constraint the_constraint = constraint_bindings.get(the_declaration);
            if (the_constraint != null) {
              return the_constraint.the_value();
            } else {
              return null;
            }
          }
      };

  private constrained_analysis_context(base_analysis_context parent,
      immutable_dictionary<variable_declaration, constraint> constraint_bindings) {
    this.parent = parent;
    this.constraint_bindings = constraint_bindings;
  }

  public static analysis_context clear_non_local(analysis_context clear_parent) {
    if (clear_parent instanceof base_analysis_context) {
      return clear_parent;
    }

    base_analysis_context new_parent = ((constrained_analysis_context) clear_parent).parent;
    readonly_list<dictionary.entry<variable_declaration, constraint>> parent_constraints =
        ((constrained_analysis_context) clear_parent).constraints().elements();

    dictionary<variable_declaration, constraint> constraint_dictionary =
        new list_dictionary<variable_declaration, constraint>();

    for (int i = 0; i < parent_constraints.size(); ++i) {
      dictionary.entry<variable_declaration, constraint> the_constraint =
          parent_constraints.get(i);
      if (the_constraint.value().is_local()) {
        constraint_dictionary.put(the_constraint.key(), the_constraint.value());
      }
    }

    if (constraint_dictionary.is_empty()) {
      return new_parent;
    }

    return new constrained_analysis_context(new_parent, constraint_dictionary.frozen_copy());
  }

  public static analysis_context combine(analysis_context combine_parent,
      readonly_list<constraint> the_constraints) {
    if (the_constraints.is_empty()) {
      return combine_parent;
    }

    base_analysis_context new_parent;
    if (combine_parent instanceof base_analysis_context) {
      new_parent = (base_analysis_context) combine_parent;
    } else {
      new_parent = ((constrained_analysis_context) combine_parent).parent;
    }

    dictionary<variable_declaration, constraint> constraint_dictionary =
        new list_dictionary<variable_declaration, constraint>();

    // TODO: implement dictionary.copy() or dictionary.add_all()
    if (combine_parent instanceof constrained_analysis_context) {
      readonly_list<dictionary.entry<variable_declaration, constraint>> parent_constraints =
          ((constrained_analysis_context) combine_parent).constraints().elements();
      for (int i = 0; i < parent_constraints.size(); ++i) {
        dictionary.entry<variable_declaration, constraint> the_constraint =
            parent_constraints.get(i);
        constraint_dictionary.put(the_constraint.key(), the_constraint.value());
      }
    }

    for (int i = 0; i < the_constraints.size(); ++i) {
      constraint the_constraint = the_constraints.get(i);
      // TODO: check that constraint isn't trivial
      //  and is either part of the declaration or part of the context.
      constraint_dictionary.put(the_constraint.the_declaration, the_constraint);
    }

    if (constraint_dictionary.is_empty()) {
      return new_parent;
    }

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
    return parent.find_promotion(from, target, constraint_mapper) != null;
  }

  @Override
  public action promote(action from, type target, origin pos) {
    // TODO: share the code with base_analysis_context
    if (from instanceof error_signal) {
      return from;
    }

    @Nullable action result = parent.find_promotion(from, target, constraint_mapper);

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
  public @Nullable readonly_list<construct> load_resource(
      type_announcement_construct the_announcement) {
    return parent.load_resource(the_announcement);
  }

  public immutable_dictionary<variable_declaration, constraint> constraints() {
    return constraint_bindings;
  }

  @Override
  public string to_string() {
    string_writer content = new string_writer();
    content.write_all(new base_string("context "));
    content.write_all(new base_string(parent.to_string(), " {"));
    readonly_list<constraint> constraints = constraints().values().elements();
    for (int i = 0; i < constraints.size(); ++i) {
      constraint the_constraint = constraints.get(i);
      content.write_all(the_constraint.to_string());
      content.write_all(new base_string(", "));
    }
    content.write_all(new base_string("}"));
    return content.elements();
  }
}

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

public class constraint_state extends debuggable
    implements function1<abstract_value, variable_declaration> {

  private final immutable_dictionary<variable_declaration, constraint> constraint_bindings;

  @Override
  public abstract_value call(variable_declaration the_declaration) {
    constraint the_constraint = constraint_bindings.get(the_declaration);
    if (the_constraint != null) {
      return the_constraint.the_value();
    } else {
      return null;
    }
  }

  private constraint_state(immutable_dictionary<variable_declaration, constraint>
      constraint_bindings) {
    this.constraint_bindings = constraint_bindings;
  }

  public @Nullable constraint_state clear_non_local() {
    readonly_list<dictionary.entry<variable_declaration, constraint>> current_constraints =
        constraint_bindings.elements();

    dictionary<variable_declaration, constraint> constraint_dictionary =
        new list_dictionary<variable_declaration, constraint>();

    for (int i = 0; i < current_constraints.size(); ++i) {
      dictionary.entry<variable_declaration, constraint> the_constraint =
          current_constraints.get(i);
      if (the_constraint.value().is_local()) {
        constraint_dictionary.put(the_constraint.key(), the_constraint.value());
      }
    }

    if (constraint_dictionary.is_empty()) {
      return null;
    } else {
      return new constraint_state(constraint_dictionary.frozen_copy());
    }
  }

  public constraint_state combine(readonly_list<constraint> new_constraints) {
    if (new_constraints.is_empty()) {
      return this;
    }

    dictionary<variable_declaration, constraint> constraint_dictionary =
        new list_dictionary<variable_declaration, constraint>();

    // TODO: implement dictionary.copy() or dictionary.add_all()
    readonly_list<dictionary.entry<variable_declaration, constraint>> current_constraints =
        constraint_bindings.elements();
    for (int i = 0; i < current_constraints.size(); ++i) {
      dictionary.entry<variable_declaration, constraint> the_constraint =
          current_constraints.get(i);
      constraint_dictionary.put(the_constraint.key(), the_constraint.value());
    }

    for (int i = 0; i < new_constraints.size(); ++i) {
      constraint the_constraint = new_constraints.get(i);
      // TODO: check that constraint isn't trivial
      //  and is either part of the declaration or part of the context.
      constraint_dictionary.put(the_constraint.the_declaration, the_constraint);
    }

    if (constraint_dictionary.is_empty()) {
      return null;
    }

    return new constraint_state(constraint_dictionary.frozen_copy());
  }

  @Override
  public string to_string() {
    string_writer content = new string_writer();
    content.write_all(new base_string("context {"));
    readonly_list<constraint> constraints = constraint_bindings.values().elements();
    for (int i = 0; i < constraints.size(); ++i) {
      constraint the_constraint = constraints.get(i);
      content.write_all(the_constraint.to_string());
      content.write_all(new base_string(", "));
    }
    content.write_all(new base_string("}"));
    return content.elements();
  }
}

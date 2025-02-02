/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class specialized_procedure extends debuggable implements procedure_declaration,
    analyzable {

  private final procedure_declaration main;
  private final type return_type;
  private final principal_type parent_type;
  private final readonly_list<variable_declaration> parameter_variables;
  private final @Nullable action body_action;
  private final variable_declaration this_declaration;
  private final readonly_list<type> argument_types;
  private final type procedure_type;
  private @Nullable action procedure_action;

  public specialized_procedure(procedure_declaration main, type return_type,
      principal_type parent_type, readonly_list<variable_declaration> parameter_variables,
      @Nullable action body_action, variable_declaration this_declaration) {
    this.main = main;
    this.return_type = return_type;
    this.parent_type = parent_type;
    this.parameter_variables = parameter_variables;
    this.body_action = body_action;
    this.this_declaration = this_declaration;

    assert main.get_category() != procedure_category.STATIC;

    list<type> type_params = new base_list<type>();
    list<abstract_value> procedure_params = new base_list<abstract_value>();
    procedure_params.append(return_type);
    for (int i = 0; i < parameter_variables.size(); ++i) {
      type param = parameter_variables.get(i).value_type();
      type_params.append(param);
      procedure_params.append(param);
    }
    argument_types = type_params;
    // TODO: use function_type() for pure fn.
    procedure_type = common_types.
        procedure_type().bind_parameters(new type_parameters(procedure_params)).
            get_flavored(flavor.immutable_flavor);
  }

  public void add(action_context context) {
    type from_type = declared_in_type().get_flavored(get_flavor());
    @Nullable overloaded_procedure the_overloaded_procedure = null;
    readonly_list<action> overloaded_actions = context.lookup(from_type, short_name());
    for (int i = 0; i < overloaded_actions.size(); ++i) {
      action overloaded_action = overloaded_actions.get(i);
      if (overloaded_action instanceof data_value_action &&
          ((data_value_action) overloaded_action).the_value instanceof overloaded_procedure) {
        // TODO: signal error instead
        assert the_overloaded_procedure == null;
        the_overloaded_procedure =
            (overloaded_procedure) ((data_value_action) overloaded_action).the_value;
        continue;
      }
    }

    procedure_action = analyzer_utilities.add_procedure(this, the_overloaded_procedure, context);
  }

  @Override
  public procedure_declaration master_declaration() {
    return main;
  }

  @Override
  public annotation_set annotations() {
    return main.annotations();
  }

  @Override
  public action_name short_name() {
    return main.short_name();
  }

  @Override
  public simple_name original_name() {
    return main.original_name();
  }

  @Override
  public type_flavor get_flavor() {
    return main.get_flavor();
  }

  @Override
  public procedure_category get_category() {
    return main.get_category();
  }

  @Override
  public type get_return_type() {
    return return_type;
  }

  @Override
  public principal_type declared_in_type() {
    return parent_type;
  }

  @Override
  public readonly_list<type> get_argument_types() {
    return argument_types;
  }

  @Override
  public readonly_list<variable_declaration> get_parameter_variables() {
    return parameter_variables;
  }

  @Override
  public type get_procedure_type() {
    return procedure_type;
  }

  @Override
  public boolean has_errors() {
    return false;
  }

  @Override
  public readonly_list<analyzable> children() {
    return new empty<analyzable>();
  }

  @Override
  public specialized_procedure specialize(specialization_context dictionary,
      principal_type new_parent) {
    utilities.panic("specialized_procedure.specialize() not implemented");
    return null;
  }

  @Override
  public analysis_result analyze() {
    // TODO: do not panic
    utilities.panic("specialized_procedure.analyze() not implemented");
    return null;
  }

  @Override
  public boolean is_pure() {
    return main.is_pure();
  }

  @Override
  public boolean overrides_variable() {
    return main.overrides_variable();
  }

  @Override
  public readonly_list<declaration> get_overriden() {
    return main.get_overriden();
  }

  @Override
  public boolean has_body() {
    return body_action != null;
  }

  @Override
  public @Nullable action get_body_action() {
    return body_action;
  }

  @Override
  public @Nullable action procedure_action() {
    return procedure_action;
  }

  @Override
  public variable_declaration get_this_declaration() {
    return this_declaration;
  }

  @Override
  public origin deeper_origin() {
    return main;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, new base_string(parent_type.short_name().toString(), ".",
        short_name().toString()));
  }
}

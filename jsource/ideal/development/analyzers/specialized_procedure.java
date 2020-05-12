/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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
  private final readonly_list<type> argument_types;
  private final type procedure_type;
  private procedure_executor result_value;

  public specialized_procedure(procedure_declaration main, type return_type,
      principal_type parent_type, readonly_list<variable_declaration> parameter_variables) {
    this.main = main;
    this.return_type = return_type;
    this.parent_type = parent_type;
    this.parameter_variables = parameter_variables;

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
    procedure_type = common_library.get_instance().
        procedure_type().bind_parameters(new type_parameters(procedure_params)).
            get_flavored(flavors.immutable_flavor);
  }

  public void add(analysis_context context) {
    result_value = new procedure_executor(this);
    analyzer_utilities.add_procedure(this, result_value, context);
  }

  public procedure_declaration get_main() {
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
  public specialized_procedure specialize(specialization_context dictionary,
      principal_type new_parent) {
    utilities.panic("specialized_procedure.specialize() not implemented");
    return null;
  }

  @Override
  public analysis_result analyze() {
    // TODO: do not panic
    utilities.panic("specialized_procedure.analyze() not implemented");
    return result_value.to_action(this);
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
  public @Nullable action get_body_action() {
    utilities.panic("specialized_procedure.get_body_action() not implemented");
    return null;
  }

  @Override
  public variable_declaration get_this_declaration() {
    utilities.panic("specialized_procedure.get_this_declaration() not implemented");
    return null;
  }

  @Override
  public origin deeper_origin() {
    return main;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}

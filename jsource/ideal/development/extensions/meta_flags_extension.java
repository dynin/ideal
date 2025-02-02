/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.extensions;

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
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;
import ideal.development.literals.*;
import ideal.development.analyzers.*;
import ideal.development.values.*;
import static ideal.development.declarations.annotation_library.*;

/**
 * Automatically generate a constructor for the flags datatype.
 */
public class meta_flags_extension extends declaration_extension {

  public static final meta_flags_extension instance = new meta_flags_extension();

  private static simple_name ARGUMENTS_NAME = simple_name.make("arguments");
  private static simple_name ERROR_REPORTER_NAME = simple_name.make("error_reporter");
  private static simple_name ARG_DICTIONARY_NAME = simple_name.make("arg_dictionary");
  private static simple_name PARSE_FLAGS_NAME = simple_name.make("parse_flags");
  private static simple_name BOOLEAN_FLAG_NAME = simple_name.make("boolean_flag");
  private static simple_name STRING_FLAG_NAME = simple_name.make("string_flag");
  private static simple_name FINISH_NAME = simple_name.make("finish");

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public meta_flags_extension() {
    super("meta_flags");
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {

    if (pass == analysis_pass.PREPARE_METHOD_AND_VARIABLE) {
      signal result = generate_constructor(the_type_declaration);
      if (result instanceof error_signal) {
        return result;
      }
    }

    return analyze(the_type_declaration, pass);
  }

  private type string_list_type() {
    return common_types.list_type_of(common_types.immutable_string_type()).get_flavored(
        flavor.readonly_flavor);
  }

  private type procedure_string_type() {
    return common_types.procedure_type().bind_parameters(
        new type_parameters(new base_list<abstract_value>(
            common_types.immutable_void_type(),
            common_types.immutable_string_type()))).get_flavored(flavor.immutable_flavor);
  }

  private principal_type flags_utilities_type() {
    return action_utilities.lookup_type(get_context(),
        new base_string("ideal.runtime.flags.flags_utilities"));
  }

  private type string_or_null_type() {
    return type_utilities.make_union(new base_list<abstract_value>(common_types.immutable_string_type(),
        common_types.immutable_null_type()));
  }

  public signal generate_constructor(type_declaration_analyzer the_type_declaration) {
    origin the_origin = this;
    readonly_list<variable_declaration> variables =
        declaration_util.get_declared_variables(the_type_declaration);
    list<variable_declaration> parameters = new base_list<variable_declaration>();

    parameters.append(new variable_analyzer(PRIVATE_MODIFIERS,
        to_analyzable(string_list_type()), ARGUMENTS_NAME, null, the_origin));
    parameters.append(new variable_analyzer(PRIVATE_MODIFIERS,
        to_analyzable(procedure_string_type()), ERROR_REPORTER_NAME, null, the_origin));

    list<analyzable> statements = new base_list<analyzable>();

    analyzable flag_utilities = to_analyzable(flags_utilities_type());
    analyzable parse_flags = new parameter_analyzer(
        new resolve_analyzer(flag_utilities, PARSE_FLAGS_NAME, the_origin),
        new base_list<analyzable>(
            new resolve_analyzer(ARGUMENTS_NAME, the_origin),
            new resolve_analyzer(ERROR_REPORTER_NAME, the_origin)
        ),
        the_origin);
    statements.append(new variable_analyzer(PRIVATE_MODIFIERS,
        null, ARG_DICTIONARY_NAME, parse_flags, the_origin));

    for (int i = 0; i < variables.size(); ++i) {
      variable_declaration variable = variables.get(i);
      the_type_declaration.analyze(variable, analysis_pass.METHOD_AND_VARIABLE_DECL);
      simple_name flag_procedure_name;

      if (variable.value_type() == common_types.immutable_boolean_type()) {
        flag_procedure_name = BOOLEAN_FLAG_NAME;
      } else if (variable.value_type() == string_or_null_type()) {
        flag_procedure_name = STRING_FLAG_NAME;
      } else {
        return new error_signal(new base_string(
            "Only 'boolean' or 'string or null' flag types supported"), variable);
      }

      action_name name = variable.short_name();
      resolve_analyzer this_name = new resolve_analyzer(special_name.THIS, the_origin);
      resolve_analyzer lhs = new resolve_analyzer(this_name, name, the_origin);
      literal the_literal = new string_literal(name.to_string(), punctuation.DOUBLE_QUOTE);
      analyzable rhs = new parameter_analyzer(
          new resolve_analyzer(flag_utilities, flag_procedure_name, the_origin),
          new base_list<analyzable>(
              new resolve_analyzer(ARG_DICTIONARY_NAME, the_origin),
              new literal_analyzer(the_literal, the_origin)
          ),
          the_origin);

      analyzable assign_field = new parameter_analyzer(
          new resolve_analyzer(operator.ASSIGN, the_origin),
          new base_list<analyzable>(lhs, rhs),
          the_origin);

      statements.append(assign_field);
    }

    analyzable finish = new parameter_analyzer(
        new resolve_analyzer(flag_utilities, FINISH_NAME, the_origin),
        new base_list<analyzable>(
            new resolve_analyzer(ARG_DICTIONARY_NAME, the_origin),
            new resolve_analyzer(ERROR_REPORTER_NAME, the_origin)
        ),
        the_origin);
    statements.append(finish);

    block_analyzer body = new block_analyzer(new list_analyzer(statements, the_origin),
        the_origin);
    procedure_analyzer constructor_procedure = new procedure_analyzer(
        PUBLIC_MODIFIERS, null, (simple_name) the_type_declaration.short_name(),
        parameters, body, the_origin);
    the_type_declaration.append_to_body(constructor_procedure);

    return ok_signal.instance;
  }
}

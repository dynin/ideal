/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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
import ideal.development.kinds.*;
import ideal.development.analyzers.*;

/**
 * Automatically generate boilerplate code for constructor data declaration.
 * <ul>
 * <li>Generate constructor.</li>
 * </ul>
 */
public class construct_data_extension extends declaration_extension {

  public static final construct_data_extension instance = new construct_data_extension();

  private static simple_name BASE_CONSTRUCT_NAME = simple_name.make("base_construct");

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public construct_data_extension() {
    super("construct_data");
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {
    signal result = analyze(the_type_declaration, pass);

    if (result instanceof ok_signal) {
      if (pass == analysis_pass.SUPERTYPE_DECL) {
        append_supertype(the_type_declaration);
      } else if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
        // append_constructor(the_type_declaration);
      }
    }

    return result;
  }

  public void append_supertype(type_declaration_analyzer the_type_declaration) {
    origin the_origin = this;
    resolve_analyzer base_construct_name = new resolve_analyzer(BASE_CONSTRUCT_NAME, the_origin);
    supertype_analyzer supertype = new supertype_analyzer(null, subtype_tags.extends_tag,
        base_construct_name, the_origin);
    the_type_declaration.append_to_body(supertype);
  }

  public void append_constructor(type_declaration_analyzer the_type_declaration) {
    origin the_origin = this;
    readonly_list<variable_declaration> variables =
        declaration_util.get_declared_variables(the_type_declaration);
    list<variable_declaration> parameters = new base_list<variable_declaration>();
    list<analyzable> statements = new base_list<analyzable>();

    for (int i = 0; i < variables.size(); ++i) {
      variable_declaration variable = variables.get(i);
      action_name name = variable.short_name();
      variable_analyzer parameter = new variable_analyzer(analyzer_utilities.PRIVATE_VAR_MODIFIERS,
          to_analyzable(variable.value_type()), name, null, the_origin);
      parameters.append(parameter);

      resolve_analyzer this_name = new resolve_analyzer(special_name.THIS, the_origin);
      resolve_analyzer lhs = new resolve_analyzer(this_name, name, the_origin);
      resolve_analyzer rhs = new resolve_analyzer(name, the_origin);

      analyzable assign_field = new parameter_analyzer(
          new resolve_analyzer(operator.ASSIGN, the_origin),
          new base_list<analyzable>(lhs, rhs),
          the_origin);

      statements.append(assign_field);
    }

    block_analyzer body = new block_analyzer(new statement_list_analyzer(statements, the_origin),
        the_origin);
    procedure_analyzer constructor_procedure = new procedure_analyzer(
        analyzer_utilities.PUBLIC_MODIFIERS, null, (simple_name) the_type_declaration.short_name(),
        parameters, body, the_origin);

    the_type_declaration.append_to_body(constructor_procedure);
  }
}

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
import ideal.development.analyzers.*;
import static ideal.development.declarations.annotation_library.*;

/**
 * Automatically generate a constructor that initializes all fields of a data type.
 * For a declaration
 * <code>
 * datatype ellipse {
 *   integer width;
 *   integer height;
 * }
 * </code>
 * this extension adds a constructor to the type
 * <code>
 * public ellipse(integer width, integer height) {
 *   this.width = width;
 *   this.height = height;
 * }
 * </code>
 */
public class auto_constructor_extension extends declaration_extension {

  public static final auto_constructor_extension instance = new auto_constructor_extension();

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public auto_constructor_extension() {
    super("auto_constructor");
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {
    if (pass == analysis_pass.PREPARE_METHOD_AND_VARIABLE) {
      the_type_declaration.append_to_body(generate_constructor(the_type_declaration));
    }

    return analyze(the_type_declaration, pass);
  }

  public procedure_analyzer generate_constructor(type_declaration_analyzer the_type_declaration) {
    origin the_origin = this;
    readonly_list<variable_declaration> variables =
        declaration_util.get_declared_variables(the_type_declaration);
    list<variable_declaration> parameters = new base_list<variable_declaration>();
    list<analyzable> statements = new base_list<analyzable>();

    for (int i = 0; i < variables.size(); ++i) {
      variable_declaration variable = variables.get(i);
      the_type_declaration.analyze(variable, analysis_pass.METHOD_AND_VARIABLE_DECL);
      action_name name = variable.short_name();
      variable_analyzer parameter = new variable_analyzer(PRIVATE_MODIFIERS,
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

    block_analyzer body = new block_analyzer(new list_analyzer(statements, the_origin),
        the_origin);
    procedure_analyzer constructor_procedure = new procedure_analyzer(
        PUBLIC_MODIFIERS, null, (simple_name) the_type_declaration.short_name(),
        parameters, body, the_origin);
    return constructor_procedure;
  }
}

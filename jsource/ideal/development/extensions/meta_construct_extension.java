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
import static ideal.development.declarations.annotation_library.*;

/**
 * Automatically generate boilerplate code for constructor data declaration.
 * <ul>
 * <li>Generate constructor.</li>
 * </ul>
 */
public class meta_construct_extension extends declaration_extension {

  public static final meta_construct_extension instance = new meta_construct_extension();

  private static simple_name LIST_NAME = simple_name.make("list");
  private static simple_name DEVELOPMENT_NAME = simple_name.make("development");
  private static simple_name ELEMENTS_NAME = simple_name.make("elements");
  private static simple_name CONSTRUCT_NAME = simple_name.make("construct");
  private static simple_name ORIGIN_NAME = simple_name.make("origin");
  private static simple_name CHILDREN_NAME = simple_name.make("children");
  private static simple_name RESULT_NAME = simple_name.make("result");
  private static simple_name BASE_CONSTRUCT_NAME = simple_name.make("base_construct");
  private static simple_name BASE_LIST_NAME = simple_name.make("base_list");
  private static simple_name APPEND_NAME = simple_name.make("append");
  private static simple_name APPEND_ALL_NAME = simple_name.make("append_all");

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public meta_construct_extension() {
    super("meta_construct");
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {

    if (pass == analysis_pass.SUPERTYPE_DECL) {
      append_supertype(the_type_declaration);
    } else if (pass == analysis_pass.PREPARE_METHOD_AND_VARIABLE) {
      append_conditions(the_type_declaration);
    }

    return analyze(the_type_declaration, pass);
  }

  private principal_type lookup(principal_type the_type, simple_name the_name) {
    readonly_list<type> types = action_utilities.lookup_types(get_context(), the_type, the_name);
    assert types.size() == 1;
    return (principal_type) types.first();
  }

  public void append_supertype(type_declaration_analyzer the_type_declaration) {
    origin the_origin = this;
    resolve_analyzer base_construct_name = new resolve_analyzer(BASE_CONSTRUCT_NAME, the_origin);
    supertype_analyzer supertype = new supertype_analyzer(null, subtype_tags.extends_tag,
        base_construct_name, the_origin);
    the_type_declaration.append_to_body(supertype);
  }

  public void append_conditions(type_declaration_analyzer the_type_declaration) {
    origin the_origin = this;
    readonly_list<variable_declaration> variables =
        declaration_util.get_declared_variables(the_type_declaration);
    list<variable_declaration> parameters = new base_list<variable_declaration>();
    list<analyzable> ctor_statements = new base_list<analyzable>();
    list<analyzable> children_statements = new base_list<analyzable>();

    simple_name generated_result_name = generated_name(RESULT_NAME);
    parameter_analyzer base_list = new parameter_analyzer(
        new resolve_analyzer(BASE_LIST_NAME, the_origin),
        new base_list<analyzable>(new resolve_analyzer(CONSTRUCT_NAME, the_origin)), the_origin);
    parameter_analyzer result_init = new parameter_analyzer(
        new resolve_analyzer(base_list, special_name.NEW, the_origin),
        new empty(), the_origin);
    variable_analyzer result_var = new variable_analyzer(PRIVATE_MODIFIERS,
        null, generated_result_name, result_init, the_origin);
    children_statements.append(result_var);

    // TODO: factor this into cacheable methods.
    common_library library = common_library.get_instance();
    principal_type null_type = library.null_type();
    principal_type development_type = lookup(library.ideal_namespace(), DEVELOPMENT_NAME);
    principal_type elements_type = lookup(development_type, ELEMENTS_NAME);
    principal_type construct_type = lookup(elements_type, CONSTRUCT_NAME);

    for (int i = 0; i < variables.size(); ++i) {
      variable_declaration variable = variables.get(i);
      the_type_declaration.analyze(variable, analysis_pass.METHOD_AND_VARIABLE_DECL);
      action_name name = variable.short_name();
      type value_type = variable.value_type();
      boolean union_field = type_utilities.is_union(value_type);

      variable_analyzer parameter = new variable_analyzer(PRIVATE_MODIFIERS,
          to_analyzable(value_type), name, null, the_origin);
      parameters.append(parameter);

      if (!union_field && introduce_assert(value_type)) {
        analyzable assert_statement = new constraint_analyzer(
            new parameter_analyzer(
                new resolve_analyzer(operator.IS_NOT_OPERATOR, the_origin),
                new base_list<analyzable>(
                    new resolve_analyzer(name, the_origin),
                    to_analyzable(null_type)
                ),
                the_origin
            ),
            the_origin);
        ctor_statements.append(assert_statement);
      }

      resolve_analyzer this_name = new resolve_analyzer(special_name.THIS, the_origin);
      resolve_analyzer lhs = new resolve_analyzer(this_name, name, the_origin);
      resolve_analyzer rhs = new resolve_analyzer(name, the_origin);

      analyzable assign_field = new parameter_analyzer(
          new resolve_analyzer(operator.ASSIGN, the_origin),
          new base_list<analyzable>(lhs, rhs),
          the_origin);

      ctor_statements.append(assign_field);

      analyzable field_access = new resolve_analyzer(name, the_origin);
      type removed_null;
      simple_name procedure_name;

      if (union_field) {
        removed_null = library.remove_null_type(value_type);
      } else {
        removed_null = value_type;
      }

      if (removed_null.principal() == construct_type) {
        procedure_name = APPEND_NAME;
        if (union_field) {
          field_access = new parameter_analyzer(
              new resolve_analyzer(operator.HARD_CAST, the_origin),
              new base_list<analyzable>(field_access, to_analyzable(removed_null)),
              the_origin);
        }
      } else {
        principal_type list_element_type = element_type(removed_null);
        if (list_element_type == null) {
          continue;
        }

        procedure_name = APPEND_ALL_NAME;
        if (union_field || list_element_type != construct_type) {
          // TODO: the cast should be redundant.
          type construct_list = library.list_type_of(
              construct_type.get_flavored(flavor.mutable_flavor)).get_flavored(
              flavor.readonly_flavor);
          field_access = new parameter_analyzer(
              new resolve_analyzer(operator.HARD_CAST, the_origin),
              new base_list<analyzable>(field_access, to_analyzable(construct_list)),
              the_origin);
        }
      }

      analyzable append_analyzable = new parameter_analyzer(
            new resolve_analyzer(new resolve_analyzer(generated_result_name, the_origin),
                procedure_name, the_origin),
            new base_list<analyzable>(field_access),
          the_origin);

      if (union_field) {
        analyzable if_statement = new conditional_analyzer(
            new parameter_analyzer(
                new resolve_analyzer(operator.IS_NOT_OPERATOR, the_origin),
                new base_list<analyzable>(
                    new resolve_analyzer(name, the_origin),
                    to_analyzable(null_type)
                ),
                the_origin
            ),
            append_analyzable,
            null,
            the_origin);
        children_statements.append(if_statement);
      } else {
        children_statements.append(append_analyzable);
      }
    }

    if (!has_constructor(the_type_declaration)) {
      simple_name generated_origin_name = generated_name(ORIGIN_NAME);
      resolve_analyzer origin_type = new resolve_analyzer(ORIGIN_NAME, the_origin);
      variable_analyzer origin_parameter = new variable_analyzer(
          PRIVATE_MODIFIERS, origin_type, generated_origin_name, null, the_origin);
      parameters.append(origin_parameter);

      resolve_analyzer super_name = new resolve_analyzer(special_name.SUPER, the_origin);
      readonly_list<analyzable> super_arguments =
          new base_list<analyzable>(new resolve_analyzer(generated_origin_name, the_origin));
      ctor_statements.prepend(new parameter_analyzer(super_name, super_arguments, the_origin));

      block_analyzer ctor_body = new block_analyzer(new list_analyzer(ctor_statements,
          the_origin), the_origin);
      procedure_analyzer constructor_procedure = new procedure_analyzer(
          PUBLIC_MODIFIERS, null, (simple_name) the_type_declaration.short_name(),
          parameters, ctor_body, the_origin);

      the_type_declaration.append_to_body(constructor_procedure);
    }

    if (!has_children_procedure(the_type_declaration)) {
      return_analyzer children_return = new return_analyzer(
          new resolve_analyzer(generated_result_name, the_origin), the_origin);
      children_statements.append(children_return);
      block_analyzer children_body = new block_analyzer(new list_analyzer(
          children_statements, the_origin), the_origin);

      analyzable children_return_type = new flavor_analyzer(flavor.readonly_flavor,
          new parameter_analyzer(new resolve_analyzer(LIST_NAME, the_origin),
              new base_list(new resolve_analyzer(CONSTRUCT_NAME, the_origin)), the_origin),
          the_origin);
      procedure_analyzer children_procedure = new procedure_analyzer(
          PUBLIC_OVERRIDE_MODIFIERS, children_return_type, CHILDREN_NAME,
          new empty<variable_declaration>(), children_body, the_origin);

      the_type_declaration.append_to_body(children_procedure);
    }
  }

  private boolean has_constructor(type_declaration the_type_declaration) {
    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(the_type_declaration);
    return procedures.has(new predicate<procedure_declaration>() {
      public @Override Boolean call(procedure_declaration the_procedure_declaration) {
        ((procedure_analyzer) the_procedure_declaration).multi_pass_analysis(
            analysis_pass.PREPARE_METHOD_AND_VARIABLE);
        return the_procedure_declaration.get_category() == procedure_category.CONSTRUCTOR;
      }
    });
  }

  private boolean has_children_procedure(type_declaration the_type_declaration) {
    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(the_type_declaration);
    return procedures.has(new predicate<procedure_declaration>() {
      public @Override Boolean call(procedure_declaration the_procedure_declaration) {
        return the_procedure_declaration.short_name() == CHILDREN_NAME;
      }
    });
  }

  private boolean introduce_assert(type the_type) {
    return the_type.principal() != common_library.get_instance().boolean_type();
  }

  private @Nullable principal_type element_type(type the_type) {
    the_type = the_type.principal();
    if (the_type instanceof parametrized_type &&
        ((parametrized_type) the_type).get_master() == common_library.get_instance().list_type()) {
      type_parameters list_parameters =
          ((parametrized_type) the_type).get_parameters();
      // List types have exactly one parameter
      assert list_parameters.is_valid_arity(1);
      return list_parameters.first().type_bound().principal();
    }
    return null;
  }
}

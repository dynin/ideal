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
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;

/**
 * Implement procedure result caching (memoization.)  For example, given the declaration
 * <code>
 * cache string foo() {
 *   return perform_expensive_operation();
 * }
 * </code>
 * this extension produces
 * <code>
 * private var string or null generated_foo_cache;
 * string foo() {
 *   var result : generated_foo_cache;
 *   if (result is null) {
 *     result = generated_foo_compute();
 *     generated_foo_cache = result;
 *   }
 *   return result;
 * }
 * private string generated_foo_compute() {
 *   return perform_expensive_operation();
 * }
 * </code>
 * For a real-world example, see |supported_flavors()| in
 * https://github.com/dynin/ideal/blob/master/development/flavors/base_flavor_profile.i
 * and the Java code it generates in
 * https://github.com/dynin/ideal/blob/master/bootstrapped/ideal/development/flavors/base_flavor_profile.java
 */
public class cache_extension extends declaration_extension {

  private static final simple_name cache_name = simple_name.make("cache");
  private static final simple_name compute_name = simple_name.make("compute");
  private static final simple_name result_name = simple_name.make("result");

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public cache_extension() {
    super("cache");
  }

  @Override
  protected @Nullable error_signal process_procedure(procedure_analyzer the_procedure,
      analysis_pass pass) {

    if (pass.is_before(analysis_pass.METHOD_AND_VARIABLE_DECL)) {
      return find_error(the_procedure, pass);
    }

    assert pass == analysis_pass.METHOD_AND_VARIABLE_DECL;
    origin the_origin = this;

    if (the_procedure.get_parameter_variables().is_not_empty()) {
     return new error_signal(new base_string("Cached procedure can't have any parameters"),
        the_origin);
    }

    simple_name procedure_name = the_procedure.original_name();
    simple_name generated_field_name =
        generated_name(name_utilities.join(procedure_name, cache_name));
    simple_name generated_procedure_name =
        generated_name(name_utilities.join(procedure_name, compute_name));

    type return_type = the_procedure.get_return_type();

    if (get_context().is_subtype_of(library().immutable_null_type(), return_type)) {
       // Since null value is used to mark uninitalized cache values,
       return new error_signal(new base_string("Typed of cache value cannot be supertype of null"),
          the_origin);
    }

    type variable_type = type_utilities.make_union(new base_list<abstract_value>(
        return_type, library().immutable_null_type()));

    // Generate the field that contains the cached value
    variable_analyzer field = new variable_analyzer(analyzer_utilities.PRIVATE_VAR_MODIFIERS,
        to_analyzable(variable_type), generated_field_name, null, the_origin);

    // Generate the procedure that encapsulates caching logic
    variable_analyzer result = new variable_analyzer(analyzer_utilities.PRIVATE_VAR_MODIFIERS,
        null, result_name, new resolve_analyzer(generated_field_name, the_origin), the_origin);
    return_analyzer return_result = new return_analyzer(
        new resolve_analyzer(result_name, the_origin), the_origin);
    analyzable condition = new parameter_analyzer(
        new resolve_analyzer(operator.IS_OPERATOR, the_origin),
        new base_list<analyzable>(new resolve_analyzer(result_name, the_origin),
            to_analyzable(library().immutable_null_type())), the_origin);
    analyzable assign_result = new parameter_analyzer(
        new resolve_analyzer(operator.ASSIGN, the_origin),
        new base_list<analyzable>(
            new resolve_analyzer(result_name, the_origin),
            new parameter_analyzer(
                new resolve_analyzer(generated_procedure_name, the_origin),
                new empty<analyzable>(),
                the_origin
            )
        ),
        the_origin
    );
    analyzable assign_cache = new parameter_analyzer(
        new resolve_analyzer(operator.ASSIGN, the_origin),
        new base_list<analyzable>(
            new resolve_analyzer(generated_field_name, the_origin),
            new resolve_analyzer(result_name, the_origin)
        ),
        the_origin
    );
    analyzable then_block = new block_analyzer(
        new statement_list_analyzer(new base_list<analyzable>(
            assign_result, assign_cache), the_origin),
        the_origin);
    analyzable if_statement = new conditional_analyzer(condition, then_block, null, the_origin);
    block_analyzer caching_body = new block_analyzer(new statement_list_analyzer(
        new base_list<analyzable>(result, if_statement, return_result),
        the_origin), the_origin);

    procedure_analyzer caching_procedure = new procedure_analyzer(
        the_procedure.annotations(), to_analyzable(return_type),
        procedure_name, new empty<variable_declaration>(),
        caching_body, the_origin);

    procedure_analyzer compute_procedure = new procedure_analyzer(
        analyzer_utilities.PRIVATE_MODIFIERS, to_analyzable(return_type), generated_procedure_name,
        new empty<variable_declaration>(), the_procedure.get_body(), the_origin);


    declaration_list_analyzer the_declaration_list = new declaration_list_analyzer(
        new base_list<analyzable>(field, caching_procedure, compute_procedure), the_origin);

    set_expanded(the_declaration_list);

    return null;
  }
}
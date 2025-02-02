/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.policies;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.literals.*;
import ideal.development.extensions.*;

/**
 * Automatically generate run_all_tests() method.
 */
public class test_suite_policy extends general_policy {

  public static final test_suite_policy instance = new test_suite_policy();

  private static final simple_name RUN_ALL_TESTS_NAME = simple_name.make("run_all_tests");
  private static final simple_name START_TEST_NAME = simple_name.make("start_test");
  private static final simple_name END_TEST_NAME = simple_name.make("end_test");

  @Override
  public void process_member_declaration(type_declaration the_type_declaration,
      action_context context) {
    origin the_origin = the_type_declaration;
    assert the_type_declaration instanceof type_declaration_analyzer;
    type_declaration_analyzer the_type_declaration_analyzer =
        (type_declaration_analyzer) the_type_declaration;

    if (!has_test_cases(the_type_declaration_analyzer)) {
      new base_notification(new base_string("No test cases in a test suite"), the_origin).report();
      return;
    }

    the_type_declaration_analyzer.append_to_body(
        generate_run_all_tests(the_type_declaration_analyzer, context, the_origin));
  }

  public procedure_analyzer generate_run_all_tests(type_declaration_analyzer the_type_declaration,
      action_context context, origin the_origin) {
    principal_type runtime_util_type = action_utilities.lookup_type(context,
        new base_string("ideal.machine.elements.runtime_util"));
    analyzable runtime_util = base_analyzable_action.from(runtime_util_type, the_origin);
    // construct start_construct = new resolve_construct(runtime_util_name, START_TEST, the_origin);
    // construct end_construct = new resolve_construct(runtime_util_name, END_TEST, the_origin);

    string type_name = the_type_declaration.short_name().to_string();
    readonly_list<analyzable> type_body = the_type_declaration.get_body();

    list<analyzable> test_calls = new base_list<analyzable>();
    for (int i = 0; i < type_body.size(); ++i) {
      analyzable the_analyzable = type_body.get(i);
      // TODO: use is_test_case?
      if (!(the_analyzable instanceof test_case_extension)) {
        continue;
      }

      procedure_declaration the_procedure =
          (procedure_declaration) ((test_case_extension) the_analyzable).get_declaration();
      simple_name test_case_name = (simple_name) the_procedure.short_name();

      string name_string = new base_string(type_name, ".", test_case_name.to_string());
      literal name_literal = new string_literal(name_string, punctuation.DOUBLE_QUOTE);
      analyzable method_name = new literal_analyzer(name_literal, the_origin);

      analyzable start_call = new parameter_analyzer(
          new resolve_analyzer(runtime_util,
              START_TEST_NAME,
              the_origin
          ),
          new base_list<analyzable>(method_name),
          the_origin
      );
      test_calls.append(start_call);

      analyzable test_call = new parameter_analyzer(
          new resolve_analyzer(
              test_case_name,
              the_origin
          ),
          new empty<analyzable>(),
          the_origin
      );
      test_calls.append(test_call);

      analyzable end_call = new parameter_analyzer(
          new resolve_analyzer(runtime_util,
              END_TEST_NAME,
              the_origin
          ),
          new empty<analyzable>(),
          the_origin
      );
      test_calls.append(end_call);
    }

    assert test_calls.is_not_empty();

    block_analyzer body_block = new block_analyzer(
        new list_analyzer(test_calls, the_origin), the_origin);
    procedure_analyzer run_all_tests_procedure = new procedure_analyzer(
        annotation_library.PUBLIC_MODIFIERS, base_analyzable_action.from(common_types.void_type(),
        the_origin), RUN_ALL_TESTS_NAME, new empty<variable_declaration>(), body_block, the_origin);
    return run_all_tests_procedure;
  }

  private static predicate<analyzable> is_test_case() {
    return new predicate<analyzable>() {
      public @Override Boolean call(analyzable the_analyzable) {
        return the_analyzable instanceof test_case_extension;
      }
    };
  }

  public boolean has_test_cases(type_declaration_analyzer the_type_declaration_analyzer) {
    return the_type_declaration_analyzer.get_body().has(is_test_case());
  }
}

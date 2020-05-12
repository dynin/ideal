/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.library.elements.*;
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
import ideal.development.literals.*;

import javax.annotation.Nullable;

public class testcase_generator {

  private static final simple_name RUN_ALL_TESTS = simple_name.make("run_all_tests");
  private static final simple_name START_TEST = simple_name.make("start_test");
  private static final simple_name END_TEST = simple_name.make("end_test");

  public static @Nullable procedure_construct process_testcases(
      type_declaration the_type_declaration) {

    if (!has_testcases(the_type_declaration)) {
      return null;
    }

    origin source = the_type_declaration;
    construct runtime_util_name = make_type(
        java_library.get_instance().runtime_util_class(), source);
    construct start_construct = new resolve_construct(runtime_util_name,
          new name_construct(START_TEST, source), source);
    construct end_construct = new resolve_construct(runtime_util_name,
          new name_construct(END_TEST, source), source);
    string type_name = the_type_declaration.short_name().to_string();

    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(the_type_declaration);

    list<construct> test_calls = new base_list<construct>();
    for (int i = 0; i < procedures.size(); ++i) {
      procedure_declaration the_procedure = procedures.get(i);
      if (the_procedure.annotations().has(general_modifier.testcase_modifier)) {
        simple_name the_name = (simple_name) the_procedure.short_name();

        string name_string = new base_string(type_name, ".", the_name.to_string());
        literal name_literal = new quoted_literal(name_string, punctuation.DOUBLE_QUOTE);
        construct method_name = new literal_construct(name_literal, source);
        construct start_call = make_call(start_construct, new base_list<construct>(method_name),
            source);
        test_calls.append(start_call);

        name_construct the_name_construct = new name_construct(the_name, source);
        construct call = make_call(the_name_construct, new empty<construct>(), source);
        test_calls.append(call);

        construct end_call = make_call(end_construct, new empty<construct>(), source);
        test_calls.append(end_call);
      }
    }

    assert test_calls.is_not_empty();

    return new procedure_construct(new base_list<annotation_construct>(
        new modifier_construct(access_modifier.public_modifier, source)),
        new name_construct(common_library.get_instance().void_type().short_name(), source),
        RUN_ALL_TESTS,
        new list_construct(new empty<construct>(), grouping_type.PARENS, source),
        new empty<annotation_construct>(), new block_construct(test_calls, source), source);
  }

  public static boolean has_testcases(type_declaration the_type_declaration) {
    // TODO: rewrite using list.has()
    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(the_type_declaration);
    for (int i = 0; i < procedures.size(); ++i) {
      if (procedures.get(i).annotations().has(general_modifier.testcase_modifier)) {
        return true;
      }
    }
    return false;
  }

  private static construct make_type(principal_type the_type, origin source) {
    immutable_list<simple_name> full_name = type_utilities.get_full_names(the_type);
    assert full_name.is_not_empty();

    @Nullable construct result = null;
    for (int i = 0; i < full_name.size(); ++i) {
      name_construct the_construct = new name_construct(full_name.get(i), source);
      if (result == null) {
        result = the_construct;
      } else {
        result = new resolve_construct(result, the_construct, source);
      }
    }

    return result;
  }

  private static construct make_call(construct main, readonly_list<construct> parameters,
      origin source) {
    return new parameter_construct(main,
        new list_construct(parameters, grouping_type.PARENS, source), source);
  }
}

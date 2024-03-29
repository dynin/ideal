// Autogenerated from runtime/tests/all_tests.i

package ideal.runtime.tests;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.characters.*;
import ideal.runtime.patterns.*;
import ideal.runtime.formats.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.calendars.*;
import ideal.runtime.graphs.*;
import ideal.runtime.logs.*;
import ideal.runtime.flags.*;

public class all_tests {
  public static void run_all_tests() {
    all_tests.run_all_runtime_tests();
  }
  public static void run_all_runtime_tests() {
    new test_array().run_all_tests();
    new test_runtime_util().run_all_tests();
    new test_string_writer().run_all_tests();
    new test_list().run_all_tests();
    new test_range().run_all_tests();
    new test_dictionary().run_all_tests();
    new test_hash_dictionary().run_all_tests();
    new test_hash_set().run_all_tests();
    new test_character_handler().run_all_tests();
    new test_elements().run_all_tests();
    new test_plain_text().run_all_tests();
    new test_markup_text().run_all_tests();
    new test_json_parser().run_all_tests();
    new test_json_printer().run_all_tests();
    new test_display().run_all_tests();
    new test_singleton_pattern().run_all_tests();
    new test_predicate_pattern().run_all_tests();
    new test_repeat_element().run_all_tests();
    new test_sequence_pattern().run_all_tests();
    new test_procedure_matcher().run_all_tests();
    new test_sequence_matcher().run_all_tests();
    new test_option_pattern().run_all_tests();
    new test_repeat_pattern().run_all_tests();
    new test_option_matcher().run_all_tests();
    new test_repeat_matcher().run_all_tests();
    new test_list_pattern().run_all_tests();
    new test_calendars().run_all_tests();
    new test_markup_grammar().run_all_tests();
    new test_resolver().run_all_tests();
    new test_output_transformer().run_all_tests();
    new test_graph().run_all_tests();
    new test_flags().run_all_tests();
  }
}

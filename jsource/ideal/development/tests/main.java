/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tests;

import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.characters.*;
import ideal.runtime.patterns.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.graphs.*;
import ideal.runtime.logs.*;
import ideal.runtime.flags.*;
import ideal.development.names.*;
import ideal.development.futures.*;
import ideal.development.origins.*;
import ideal.development.documenters.*;

public class main {

  public static void main(String[] args) {
    run_all_runtime_tests();
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

    new test_markup_grammar().run_all_tests();

    new test_resolver().run_all_tests();

    new test_output_transformer().run_all_tests();

    new test_graph().run_all_tests();

    new test_flags().run_all_tests();

    new test_names().run_all_tests();

    new test_origin_printer().run_all_tests();

    new test_futures().run_all_tests();

    new test_doc_grammar().run_all_tests();
  }
}

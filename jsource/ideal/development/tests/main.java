/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.runtime.logs.test_display;
import ideal.development.names.*;
import ideal.development.futures.*;
import ideal.development.origins.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class main {

  public static void main(String[] args) {
    run_all_runtime_tests();

    TestSuite suite = new TestSuite();

    suite.addTestSuite(flag_util_t.class);

    junit.textui.TestRunner.run(suite);
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
    new test_markup_grammar().run_all_tests();

    new test_display().run_all_tests();

    new test_singleton_pattern().run_all_tests();
    new test_predicate_pattern().run_all_tests();

    new test_resolver().run_all_tests();

    new test_output_transformer().run_all_tests();

    new test_graph().run_all_tests();

    new test_names().run_all_tests();

    new test_origin_printer().run_all_tests();

    new test_futures().run_all_tests();
  }
}

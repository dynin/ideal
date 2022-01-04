-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Main entry point for running unittests.
namespace all_tests {
  run_all_tests() {
    ideal.runtime.tests.all_tests.run_all_runtime_tests();
    run_all_development_tests();
  }

  run_all_development_tests() {
    test_names.new().run_all_tests();

    test_origin_printer.new().run_all_tests();

    test_futures.new().run_all_tests();

    test_doc_grammar.new().run_all_tests();
  }
}

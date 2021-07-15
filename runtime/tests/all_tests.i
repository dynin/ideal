-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Main entry point for running runtime unittests.
namespace all_tests {
  void run_all_tests() {
    run_all_runtime_tests();
  }

  void run_all_runtime_tests() {
    test_array.new().run_all_tests();
    test_runtime_util.new().run_all_tests();
    test_string_writer.new().run_all_tests();

    test_list.new().run_all_tests();
    test_range.new().run_all_tests();
    test_dictionary.new().run_all_tests();
    test_hash_dictionary.new().run_all_tests();
    test_hash_set.new().run_all_tests();

    test_character_handler.new().run_all_tests();

    test_elements.new().run_all_tests();
    test_plain_text.new().run_all_tests();
    test_markup_text.new().run_all_tests();

    test_json_parser.new().run_all_tests();

    test_display.new().run_all_tests();

    test_singleton_pattern.new().run_all_tests();
    test_predicate_pattern.new().run_all_tests();
    test_repeat_element.new().run_all_tests();
    test_sequence_pattern.new().run_all_tests();
    test_procedure_matcher.new().run_all_tests();
    test_sequence_matcher.new().run_all_tests();
    test_option_pattern.new().run_all_tests();
    test_repeat_pattern.new().run_all_tests();
    test_option_matcher.new().run_all_tests();
    test_repeat_matcher.new().run_all_tests();
    test_list_pattern.new().run_all_tests();

    test_markup_grammar.new().run_all_tests();

    test_resolver.new().run_all_tests();

    test_output_transformer.new().run_all_tests();

    test_graph.new().run_all_tests();

    test_flags.new().run_all_tests();
  }
}

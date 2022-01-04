-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of patterns and matchers.
namespace patterns {
  implicit import ideal.library.elements;
  implicit import ideal.library.patterns;
  implicit import ideal.runtime.elements;

  interface validatable;
  class base_pattern;
  class one_pattern;
  class singleton_pattern;
  class predicate_pattern;
  class base_repeat_element;
  class repeat_element;
  class sequence_pattern;
  class procedure_matcher;
  class sequence_matcher;
  class option_pattern;
  class repeat_pattern;
  class option_matcher;
  class repeat_matcher;
  class list_pattern;

  test_suite test_singleton_pattern;
  test_suite test_predicate_pattern;
  test_suite test_repeat_element;
  test_suite test_sequence_pattern;
  test_suite test_procedure_matcher;
  test_suite test_sequence_matcher;
  test_suite test_option_pattern;
  test_suite test_repeat_pattern;
  test_suite test_option_matcher;
  test_suite test_repeat_matcher;
  test_suite test_list_pattern;
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of types related to structured text (markup).
namespace texts {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.library.texts;
  implicit import ideal.runtime.elements;
  import ideal.library.channels.output;

  class base_namespace;
  class base_element_id;
  class base_attribute_id;
  class text_entity;

  class base_element;
  class base_list_text_node;
  class base_list_attribute_fragment;
  class underline_style;
  class text_visitor;
  class text_rewriter;

  class text_formatter;
  class plain_formatter;
  class markup_formatter;

  class attribute_state;
  class markup_grammar;
  class markup_parser;

  namespace text_library;
  namespace text_utilities;

  test_suite test_elements;
  test_suite test_plain_text;
  test_suite test_markup_text;
  test_suite test_markup_grammar;
}

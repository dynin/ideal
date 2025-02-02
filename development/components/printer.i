-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.library.texts.text_fragment;

interface printer {
  extends value;

  text_fragment print(construct c) pure;
  text_fragment print_space;
  text_fragment print_simple_name(simple_name name) pure;
  text_fragment print_line(text_fragment fragment) pure;
  text_fragment print_word(token_type word) pure;
  text_fragment print_punctuation(token_type punct) pure;
  text_fragment print_indented_statement(construct c) pure;
  text_fragment print_grouping_in_statement(text_fragment text) pure;
}

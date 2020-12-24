-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_markup_grammar {
  import ideal.machine.characters.normal_handler;

  testcase test_simple_parse() {
    the_markup_matcher : markup_grammar.new(normal_handler.instance).document();

    input : "  markup  ";
    output : the_markup_matcher.parse(input);

    assert output == "markup";
  }
}

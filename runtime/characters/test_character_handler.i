-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_character_handler {
  import ideal.machine.characters.normal_handler;

  testcase predicate_test() {
    the_character_handler : normal_handler.instance;

    assert the_character_handler.is_letter('x');
    assert !the_character_handler.is_letter('6');

    assert the_character_handler.is_letter_or_digit('x');
    assert the_character_handler.is_letter_or_digit('8');
    assert !the_character_handler.is_letter_or_digit(' ');

    assert the_character_handler.is_whitespace(' ');
    assert !the_character_handler.is_whitespace('x');
    assert !the_character_handler.is_whitespace('4');

    assert the_character_handler.is_upper_case('X');
    assert !the_character_handler.is_upper_case('x');
    assert !the_character_handler.is_upper_case('5');
  }

  testcase conversion_test() {
    the_character_handler : normal_handler.instance;

    assert the_character_handler.to_lower_case('X') == 'x';
    assert the_character_handler.to_lower_case('x') == 'x';
    assert the_character_handler.to_lower_case('5') == '5';
  }
}

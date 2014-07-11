-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import java.lang.String;
import ideal.machine.elements.runtime_util;

class utilities {

  -- do not instantiate
  -- TODO: convert utilities to namespace.
  private utilities() { }

  static String s(string the_string) {
    return base_string.unbox(the_string);
  }

  static boolean eq(readonly equality_comparable or null first,
      readonly equality_comparable or null second) {

    first_null : first is null;
    second_null : second is null;

    if (first_null && second_null) {
      panic("double nulls in comparison");
    }

    if (first_null || second_null) {
      return false;
    }

    return runtime_util.default_equivalence(first, second);
  }

  static open_bracket : "[";
  static close_bracket : "]";
  static colon : ": ";

  static string describe(readonly value the_value) {
    return base_string.new(open_bracket, runtime_util.value_identifier(the_value), close_bracket);
  }

  static string describe(readonly value the_value, readonly convertible_to_string details) {
    if (details is null) {
      return describe(the_value);
    } else {
      return base_string.new(open_bracket, runtime_util.value_identifier(the_value), colon,
          details.to_string, close_bracket);
    }
  }

  static noreturn void panic(string message) {
    runtime_util.do_panic(s(message));
  }

  static noreturn void panic(String message) {
    runtime_util.do_panic(message);
  }
}
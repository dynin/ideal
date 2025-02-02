-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.machine.adapters.java.builtins.char;
import ideal.machine.adapters.java.lang.Object;
import ideal.machine.adapters.java.lang.String;
import ideal.machine.adapters.java.lang.StringBuilder;
import ideal.machine.channels.string_writer;

class base_string {
  extends debuggable;
  implements string;

  private String state;

  overload base_string(String s1) {
    this.state = s1;
  }

  overload base_string(String s1, String s2) {
    this.state = s1 ++ s2;
  }

  overload base_string(String s1, String s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(String s1, String s2, String s3, String s4) {
    this.state = c(c(c(s1, s2), s3), s4);
  }

  overload base_string(String s1, String s2, String s3, String s4, String s5) {
    this.state = c(c(c(c(s1, s2), s3), s4), s5);
  }

  overload base_string(String s1, String s2, String s3, String s4, String s5, String s6) {
    this.state = c(c(c(c(c(s1, s2), s3), s4), s5), s6);
  }

  overload base_string(String s1, String s2, String s3, String s4, String s5, String s6,
      String s7) {
    this.state = c(c(c(c(c(c(s1, s2), s3), s4), s5), s6), s7);
  }

  overload base_string(string s1, string s2) {
    this.state = unbox(s1) ++ unbox(s2);
  }

  overload base_string(string s1, string s2, string s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(string s1, string s2, string s3, string s4) {
    this.state = c(c(c(s1, s2), s3), s4);
  }

  overload base_string(string s1, string s2, string s3, string s4, string s5) {
    this.state = c(c(c(c(s1, s2), s3), s4), s5);
  }

  overload base_string(string s1, string s2, string s3, string s4, string s5, string s6) {
    this.state = c(c(c(c(c(s1, s2), s3), s4), s5), s6);
  }

  overload base_string(String s1, string s2) {
    this.state = s1 ++ unbox(s2);
  }

  overload base_string(string s1, String s2) {
    this.state = unbox(s1) ++ s2;
  }

  overload base_string(string s1, String s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(String s1, string s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(String s1, String s2, string s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(string s1, String s2, string s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(string s1, string s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  overload base_string(String s1, string s2, String s3, String s4, String s5) {
    this.state = c(c(c(c(s1, s2), s3), s4), s5);
  }

  String s() {
    return state;
  }

  static String unbox(string the_string) {
    return (the_string !> base_string).s();
  }

  static overload String c(String s1, String s2) {
    return (s1 ++ s2) !> String;
  }

  static overload String c(String s1, string s2) {
    return (s1 ++ unbox(s2)) !> String;
  }

  static overload String c(string s1, string s2) {
    return (unbox(s1) ++ unbox(s2)) !> String;
  }

  static overload String c(string s1, String s2) {
    return (unbox(s1) ++ s2) !> String;
  }

  static string from_list(readonly list[character] chars) {
    the_writer : string_writer.new();
    the_writer.write_all(chars);
    return the_writer.elements;
  }

  implement nonnegative size => state.length() !> nonnegative;

  implement boolean is_empty => state.length() == 0;

  implement boolean is_not_empty => state.length() != 0;

  implement character first() {
    assert is_not_empty;
    return state.charAt(0);
  }

  implement character last() {
    assert is_not_empty;
    return state.charAt(state.length() - 1);
  }

  implement implicit readonly reference[character] get(nonnegative index) pure {
    return state.charAt(index);
  }

  implement immutable list[character] elements => this;

  implement immutable list[character] frozen_copy => this;

  implement string skip(nonnegative count) {
    return base_string.new(state.substring(count));
  }

  implement string slice(nonnegative begin, nonnegative end) {
    return base_string.new(state.substring(begin, end));
  }

  implement string reversed() immutable {
    -- There is no String.reverse() in Java.  Yeah.
    return base_string.new(StringBuilder.new(state).reverse().toString());
  }

  implement boolean has(predicate[character] the_predicate) pure {
    for (var nonnegative index : 0; index < state.length(); index += 1) {
      if (the_predicate(state.charAt(index))) {
        return true;
      }
    }

    return false;
  }

  implement range indexes => base_range.new(0, size);

  -- TODO: remove this once tests are rewritten.
  override boolean equals(Object other) {
    return other is base_string && this.state.equals((other !> base_string).state);
  }

  implement string to_string() {
    return this;
  }
}

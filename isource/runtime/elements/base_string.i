-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.texts.string_text_node;
import java.builtins.char;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuffer;

class base_string {
  extends debuggable;
  implements string;
  implements string_text_node;

  private String state;

  base_string(String s1) {
    this.state = s1;
  }

  base_string(String s1, String s2) {
    this.state = s1 ++ s2;
  }

  base_string(String s1, String s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(String s1, String s2, String s3, String s4) {
    this.state = c(c(c(s1, s2), s3), s4);
  }

  base_string(String s1, String s2, String s3, String s4, String s5) {
    this.state = c(c(c(c(s1, s2), s3), s4), s5);
  }

  base_string(String s1, String s2, String s3, String s4, String s5, String s6) {
    this.state = c(c(c(c(c(s1, s2), s3), s4), s5), s6);
  }

  base_string(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
    this.state = c(c(c(c(c(c(s1, s2), s3), s4), s5), s6), s7);
  }

  base_string(string s1, string s2) {
    this.state = unbox(s1) ++ unbox(s2);
  }

  base_string(string s1, string s2, string s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(string s1, string s2, string s3, string s4) {
    this.state = c(c(c(s1, s2), s3), s4);
  }

  base_string(string s1, string s2, string s3, string s4, string s5) {
    this.state = c(c(c(c(s1, s2), s3), s4), s5);
  }

  base_string(string s1, string s2, string s3, string s4, string s5, string s6) {
    this.state = c(c(c(c(c(s1, s2), s3), s4), s5), s6);
  }

  base_string(String s1, string s2) {
    this.state = s1 ++ unbox(s2);
  }

  base_string(string s1, String s2) {
    this.state = unbox(s1) ++ s2;
  }

  base_string(string s1, String s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(String s1, string s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(String s1, String s2, string s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(string s1, String s2, string s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(string s1, string s2, String s3) {
    this.state = c(c(s1, s2), s3);
  }

  base_string(String s1, string s2, String s3, String s4, String s5) {
    this.state = c(c(c(c(s1, s2), s3), s4), s5);
  }

  String s() {
    return state;
  }

  static String unbox(string the_string) {
    return (the_string as base_string).s();
  }

  static String c(String s1, String s2) {
    return (s1 ++ s2) as String;
  }

  static String c(String s1, string s2) {
    return (s1 ++ unbox(s2)) as String;
  }

  static String c(string s1, string s2) {
    return (unbox(s1) ++ unbox(s2)) as String;
  }

  static String c(string s1, String s2) {
    return (unbox(s1) ++ s2) as String;
  }

  implement integer size() {
    return state.length();
  }

  implement boolean is_empty() {
    return state.length() == 0;
  }

  implement implicit readonly reference[character] get(nonnegative index) pure {
    return state.charAt(index);
  }

  implement immutable list[character] elements() {
    return this;
  }

  implement immutable list[character] frozen_copy() {
    return this;
  }

  implement string slice(nonnegative begin, nonnegative end) {
    return base_string.new(state.substring(begin, end));
  }

  implement string slice(nonnegative begin) {
    return base_string.new(state.substring(begin));
  }

  implement string reverse() {
    -- There is no String.reverse() in Java.  Yeah.
    return base_string.new(StringBuffer.new(state).reverse().toString());
  }

  -- TODO: remove this once tests are rewritten.
  override boolean equals(Object other) {
    return other is base_string && this.state.equals((other as base_string).state);
  }

  implement string to_string() {
    return this;
  }
}

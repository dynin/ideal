/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.characters;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class unicode_handler implements character_handler {
  public static final unicode_handler instance = new unicode_handler();

  @Override
  public boolean is_letter(char the_character) {
    return Character.isLetter(the_character);
  }

  @Override
  public boolean is_letter_or_digit(char the_character) {
    return Character.isLetterOrDigit(the_character);
  }

  @Override
  public boolean is_whitespace(char the_character) {
    return Character.isWhitespace(the_character);
  }

  @Override
  public boolean is_upper_case(char the_character) {
    return Character.isUpperCase(the_character);
  }

  @Override
  public boolean is_digit(char the_character) {
    return Character.isDigit(the_character);
  }

  @Override
  public @Nullable Integer from_digit(char the_character, Integer radix) {
    int result = Character.digit(the_character, radix);
    return result >= 0 ? result : null;
  }

  @Override
  public char to_lower_case(char c) {
    return Character.toLowerCase(c);
  }

  @Override
  public char to_upper_case(char c) {
    return Character.toUpperCase(c);
  }

  @Override
  public string to_lower_case_all(string the_string) {
    String s = utilities.s(the_string);
    String s_lower = s.toLowerCase();
    return s == s_lower ? the_string : new base_string(s_lower);
  }

  @Override
  public string to_upper_case_all(string the_string) {
    String s = utilities.s(the_string);
    String s_upper = s.toUpperCase();
    return s == s_upper ? the_string : new base_string(s_upper);
  }

  @Override
  public Integer to_code(char the_character) {
    return (int) the_character;
  }

  @Override
  public char from_code(Integer code) {
    assert code >= 0 && code <= 0xFFFF;
    return (char) (int) code;
  }
}

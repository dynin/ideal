/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.development.elements.*;
import generated.ideal.development.parsers.base_symbols;

public interface punctuation {

  // Naming convention from http://catb.org/jargon/html/A/ASCII.html

  punctuation_type SINGLE_QUOTE = new punctuation_type("'");
  punctuation_type DOUBLE_QUOTE = new punctuation_type("\"");

  punctuation_type HASH = new punctuation_type("#", base_symbols.HASH);

  punctuation_type ELLIPSIS = new punctuation_type("...");

  punctuation_type OPEN_PARENTHESIS = new punctuation_type("(", base_symbols.OPEN_PARENTHESIS);
  punctuation_type CLOSE_PARENTHESIS = new punctuation_type(")", base_symbols.CLOSE_PARENTHESIS);
  punctuation_type OPEN_BRACKET = new punctuation_type("[", base_symbols.OPEN_BRACKET);
  punctuation_type CLOSE_BRACKET = new punctuation_type("]", base_symbols.CLOSE_BRACKET);
  punctuation_type OPEN_BRACE = new punctuation_type("{", base_symbols.OPEN_BRACE);
  punctuation_type CLOSE_BRACE = new punctuation_type("}", base_symbols.CLOSE_BRACE);
  punctuation_type DOT = new punctuation_type(".", base_symbols.DOT);
  punctuation_type COMMA = new punctuation_type(",", base_symbols.COMMA);
  punctuation_type COLON = new punctuation_type(":", base_symbols.COLON);
  punctuation_type SEMICOLON = new punctuation_type(";", base_symbols.SEMICOLON);
  punctuation_type EQUALS_GREATER_THAN = new punctuation_type("=>",
      base_symbols.EQUALS_GREATER_THAN);
  punctuation_type QUESTION_MARK = new punctuation_type("?", base_symbols.QUESTION_MARK);

  punctuation_type EQUALS = new punctuation_type("=", base_symbols.EQUALS);

  punctuation_type ASTERISK = new punctuation_type("*", base_symbols.ASTERISK);
  punctuation_type SLASH = new punctuation_type("/", base_symbols.SLASH);
  punctuation_type PERCENT = new punctuation_type("%", base_symbols.PERCENT);
  punctuation_type PLUS = new punctuation_type("+", base_symbols.PLUS);
  punctuation_type PLUS_PLUS = new punctuation_type("++", base_symbols.PLUS_PLUS);
  punctuation_type PLUS_PLUS_EQUALS = new punctuation_type("++=", base_symbols.PLUS_PLUS_EQUALS);
  punctuation_type MINUS = new punctuation_type("-", base_symbols.MINUS);
  punctuation_type EQUALS_EQUALS = new punctuation_type("==", base_symbols.EQUALS_EQUALS);
  punctuation_type EXCLAMATION_MARK = new punctuation_type("!", base_symbols.EXCLAMATION_MARK);
  punctuation_type EXCLAMATION_MARK_EQUALS =
      new punctuation_type("!=", base_symbols.EXCLAMATION_MARK_EQUALS);
  punctuation_type LESS_THAN = new punctuation_type("<", base_symbols.LESS_THAN);
  punctuation_type GREATER_THAN = new punctuation_type(">", base_symbols.GREATER_THAN);
  punctuation_type LESS_THAN_EQUALS = new punctuation_type("<=", base_symbols.LESS_THAN_EQUALS);
  punctuation_type GREATER_THAN_EQUALS = new punctuation_type(">=",
      base_symbols.GREATER_THAN_EQUALS);
  punctuation_type MINUS_MINUS = new punctuation_type("--" /*, base_symbols.MINUS_MINUS*/);
  punctuation_type AMPERSAND = new punctuation_type("&", base_symbols.AMPERSAND);
  punctuation_type CARET = new punctuation_type("^", base_symbols.CARET);
  punctuation_type VERTICAL_BAR = new punctuation_type("|", base_symbols.VERTICAL_BAR);
  punctuation_type AMPERSAND_AMPERSAND = new punctuation_type("&&",
      base_symbols.AMPERSAND_AMPERSAND);
  punctuation_type VERTICAL_BAR_VERTICAL_BAR =
      new punctuation_type("||", base_symbols.VERTICAL_BAR_VERTICAL_BAR);
  punctuation_type PLUS_EQUALS = new punctuation_type("+=", base_symbols.PLUS_EQUALS);
  punctuation_type MINUS_EQUALS = new punctuation_type("-=", base_symbols.MINUS_EQUALS);
  punctuation_type ASTERISK_EQUALS = new punctuation_type("*=", base_symbols.ASTERISK_EQUALS);

  punctuation_type MINUS_MINUS_MINUS = new punctuation_type("---");
}

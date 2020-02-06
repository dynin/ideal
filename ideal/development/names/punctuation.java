/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.development.elements.*;
import generated.ideal.development.parsers.base_symbols;

public interface punctuation extends token_type {

  // Naming convention from http://catb.org/jargon/html/A/ASCII.html

  token_type SINGLE_QUOTE = new base_token_type("'");
  token_type DOUBLE_QUOTE = new base_token_type("\"");

  token_type HASH = new base_token_type("#", base_symbols.HASH);

  token_type ELLIPSIS = new base_token_type("...");

  token_type OPEN_PARENTHESIS = new base_token_type("(", base_symbols.OPEN_PARENTHESIS);
  token_type CLOSE_PARENTHESIS = new base_token_type(")", base_symbols.CLOSE_PARENTHESIS);
  token_type OPEN_BRACKET = new base_token_type("[", base_symbols.OPEN_BRACKET);
  token_type CLOSE_BRACKET = new base_token_type("]", base_symbols.CLOSE_BRACKET);
  token_type OPEN_BRACE = new base_token_type("{", base_symbols.OPEN_BRACE);
  token_type CLOSE_BRACE = new base_token_type("}", base_symbols.CLOSE_BRACE);
  token_type DOT = new base_token_type(".", base_symbols.DOT);
  token_type COMMA = new base_token_type(",", base_symbols.COMMA);
  token_type COLON = new base_token_type(":", base_symbols.COLON);
  token_type SEMICOLON = new base_token_type(";", base_symbols.SEMICOLON);
  token_type EQUALS_GREATER_THAN = new base_token_type("=>", base_symbols.EQUALS_GREATER_THAN);
  token_type QUESTION_MARK = new base_token_type("?", base_symbols.QUESTION_MARK);

  token_type EQUALS = new base_token_type("=", base_symbols.EQUALS);

  token_type ASTERISK = new base_token_type("*", base_symbols.ASTERISK);
  token_type SLASH = new base_token_type("/", base_symbols.SLASH);
  token_type PERCENT = new base_token_type("%", base_symbols.PERCENT);
  token_type PLUS = new base_token_type("+", base_symbols.PLUS);
  token_type PLUS_PLUS = new base_token_type("++", base_symbols.PLUS_PLUS);
  token_type PLUS_PLUS_EQUALS = new base_token_type("++=", base_symbols.PLUS_PLUS_EQUALS);
  token_type MINUS = new base_token_type("-", base_symbols.MINUS);
  token_type EQUALS_EQUALS = new base_token_type("==", base_symbols.EQUALS_EQUALS);
  token_type EXCLAMATION_MARK = new base_token_type("!", base_symbols.EXCLAMATION_MARK);
  token_type EXCLAMATION_MARK_EQUALS =
      new base_token_type("!=", base_symbols.EXCLAMATION_MARK_EQUALS);
  token_type LESS_THAN = new base_token_type("<", base_symbols.LESS_THAN);
  token_type GREATER_THAN = new base_token_type(">", base_symbols.GREATER_THAN);
  token_type LESS_THAN_EQUALS = new base_token_type("<=", base_symbols.LESS_THAN_EQUALS);
  token_type GREATER_THAN_EQUALS = new base_token_type(">=", base_symbols.GREATER_THAN_EQUALS);
  token_type MINUS_MINUS = new base_token_type("--" /*, base_symbols.MINUS_MINUS*/);
  token_type AMPERSAND = new base_token_type("&", base_symbols.AMPERSAND);
  token_type CARET = new base_token_type("^", base_symbols.CARET);
  token_type VERTICAL_BAR = new base_token_type("|", base_symbols.VERTICAL_BAR);
  token_type AMPERSAND_AMPERSAND = new base_token_type("&&", base_symbols.AMPERSAND_AMPERSAND);
  token_type VERTICAL_BAR_VERTICAL_BAR =
      new base_token_type("||", base_symbols.VERTICAL_BAR_VERTICAL_BAR);
  token_type PLUS_EQUALS = new base_token_type("+=", base_symbols.PLUS_EQUALS);
  token_type MINUS_EQUALS = new base_token_type("-=", base_symbols.MINUS_EQUALS);
  token_type ASTERISK_EQUALS = new base_token_type("*=", base_symbols.ASTERISK_EQUALS);

  token_type MINUS_MINUS_MINUS = new base_token_type("---");
}

-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- All punctuation token types.
--- Naming convention from http://catb.org/jargon/html/A/ASCII.html
namespace punctuation {

  SINGLE_QUOTE : punctuation_type.new("'");
  DOUBLE_QUOTE : punctuation_type.new("\"");

  HASH : punctuation_type.new("#", base_symbols.HASH);

  ELLIPSIS : punctuation_type.new("...");

  OPEN_PARENTHESIS : punctuation_type.new("(", base_symbols.OPEN_PARENTHESIS);
  CLOSE_PARENTHESIS : punctuation_type.new(")", base_symbols.CLOSE_PARENTHESIS);
  OPEN_BRACKET : punctuation_type.new("[", base_symbols.OPEN_BRACKET);
  CLOSE_BRACKET : punctuation_type.new("]", base_symbols.CLOSE_BRACKET);
  OPEN_BRACE : punctuation_type.new("{", base_symbols.OPEN_BRACE);
  CLOSE_BRACE : punctuation_type.new("}", base_symbols.CLOSE_BRACE);
  DOT : punctuation_type.new(".", base_symbols.DOT);
  COMMA : punctuation_type.new(",", base_symbols.COMMA);
  COLON : punctuation_type.new(":", base_symbols.COLON);
  SEMICOLON : punctuation_type.new(";", base_symbols.SEMICOLON);
  EQUALS_GREATER_THAN : punctuation_type.new("=>", base_symbols.EQUALS_GREATER_THAN);
  QUESTION_MARK : punctuation_type.new("?", base_symbols.QUESTION_MARK);

  EQUALS : punctuation_type.new("=", base_symbols.EQUALS);

  ASTERISK : punctuation_type.new("*", base_symbols.ASTERISK);
  SLASH : punctuation_type.new("/", base_symbols.SLASH);
  PERCENT : punctuation_type.new("%", base_symbols.PERCENT);
  PLUS : punctuation_type.new("+", base_symbols.PLUS);
  PLUS_PLUS : punctuation_type.new("++", base_symbols.PLUS_PLUS);
  PLUS_PLUS_EQUALS : punctuation_type.new("++=", base_symbols.PLUS_PLUS_EQUALS);
  MINUS : punctuation_type.new("-", base_symbols.MINUS);
  EQUALS_EQUALS : punctuation_type.new("==", base_symbols.EQUALS_EQUALS);
  EXCLAMATION_MARK : punctuation_type.new("!", base_symbols.EXCLAMATION_MARK);
  EXCLAMATION_MARK_EQUALS : punctuation_type.new("!=", base_symbols.EXCLAMATION_MARK_EQUALS);
  LESS_THAN : punctuation_type.new("<", base_symbols.LESS_THAN);
  GREATER_THAN : punctuation_type.new(">", base_symbols.GREATER_THAN);
  LESS_THAN_EQUALS : punctuation_type.new("<=", base_symbols.LESS_THAN_EQUALS);
  GREATER_THAN_EQUALS : punctuation_type.new(">=", base_symbols.GREATER_THAN_EQUALS);
  MINUS_MINUS : punctuation_type.new("--"); -- base_symbols.MINUS_MINUS
  AMPERSAND : punctuation_type.new("&", base_symbols.AMPERSAND);
  CARET : punctuation_type.new("^", base_symbols.CARET);
  VERTICAL_BAR : punctuation_type.new("|", base_symbols.VERTICAL_BAR);
  AMPERSAND_AMPERSAND : punctuation_type.new("&&", base_symbols.AMPERSAND_AMPERSAND);
  VERTICAL_BAR_VERTICAL_BAR : punctuation_type.new("||", base_symbols.VERTICAL_BAR_VERTICAL_BAR);
  PLUS_EQUALS : punctuation_type.new("+=", base_symbols.PLUS_EQUALS);
  MINUS_EQUALS : punctuation_type.new("-=", base_symbols.MINUS_EQUALS);
  ASTERISK_EQUALS : punctuation_type.new("*=", base_symbols.ASTERISK_EQUALS);

  MINUS_MINUS_MINUS : punctuation_type.new("---");
}

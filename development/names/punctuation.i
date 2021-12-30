-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- All punctuation token types.
--- Naming convention from http://catb.org/jargon/html/A/ASCII.html
namespace punctuation {

  SINGLE_QUOTE : quote_type.new('\'', "SINGLE_QUOTE");
  DOUBLE_QUOTE : quote_type.new('"', "DOUBLE_QUOTE");

  HASH : punctuation_type.new("#", "HASH");

  ELLIPSIS : punctuation_type.new("...", "ELLIPSIS");

  OPEN_PARENTHESIS : punctuation_type.new("(", "OPEN_PARENTHESIS");
  CLOSE_PARENTHESIS : punctuation_type.new(")", "CLOSE_PARENTHESIS");
  OPEN_BRACKET : punctuation_type.new("[", "OPEN_BRACKET");
  CLOSE_BRACKET : punctuation_type.new("]", "CLOSE_BRACKET");
  OPEN_BRACE : punctuation_type.new("{", "OPEN_BRACE");
  CLOSE_BRACE : punctuation_type.new("}", "CLOSE_BRACE");
  DOT : punctuation_type.new(".", "DOT");
  COMMA : punctuation_type.new(",", "COMMA");
  COLON : punctuation_type.new(":", "COLON");
  SEMICOLON : punctuation_type.new(";", "SEMICOLON");
  EQUALS_GREATER_THAN : punctuation_type.new("=>", "EQUALS_GREATER_THAN");
  QUESTION_MARK : punctuation_type.new("?", "QUESTION_MARK");

  EQUALS : punctuation_type.new("=", "EQUALS");

  ASTERISK : punctuation_type.new("*", "ASTERISK");
  SLASH : punctuation_type.new("/", "SLASH");
  PERCENT : punctuation_type.new("%", "PERCENT");
  PLUS : punctuation_type.new("+", "PLUS");
  PLUS_PLUS : punctuation_type.new("++", "PLUS_PLUS");
  PLUS_PLUS_EQUALS : punctuation_type.new("++=", "PLUS_PLUS_EQUALS");
  MINUS : punctuation_type.new("-", "MINUS");
  EQUALS_EQUALS : punctuation_type.new("==", "EQUALS_EQUALS");
  EXCLAMATION_MARK : punctuation_type.new("!", "EXCLAMATION_MARK");
  EXCLAMATION_MARK_EQUALS : punctuation_type.new("!=", "EXCLAMATION_MARK_EQUALS");
  LESS_THAN : punctuation_type.new("<", "LESS_THAN");
  GREATER_THAN : punctuation_type.new(">", "GREATER_THAN");
  LESS_THAN_EQUALS : punctuation_type.new("<=", "LESS_THAN_EQUALS");
  GREATER_THAN_EQUALS : punctuation_type.new(">=", "GREATER_THAN_EQUALS");
  LESS_THAN_EQUALS_GREATER_THAN : punctuation_type.new("<=>",
      "LESS_THAN_EQUALS_GREATER_THAN");
  DOT_GREATER_THAN : punctuation_type.new(".>", "DOT_GREATER_THAN");
  EXCLAMATION_GREATER_THAN : punctuation_type.new("!>", "EXCLAMATION_GREATER_THAN");
  QUESTION_MARK_GREATER_THAN : punctuation_type.new("?>", "QUESTION_MARK_GREATER_THAN");
  MINUS_MINUS : punctuation_type.new("--", "MINUS_MINUS");
  AMPERSAND : punctuation_type.new("&", "AMPERSAND");
  CARET : punctuation_type.new("^", "CARET");
  VERTICAL_BAR : punctuation_type.new("|", "VERTICAL_BAR");
  AMPERSAND_AMPERSAND : punctuation_type.new("&&", "AMPERSAND_AMPERSAND");
  VERTICAL_BAR_VERTICAL_BAR : punctuation_type.new("||", "VERTICAL_BAR_VERTICAL_BAR");
  PLUS_EQUALS : punctuation_type.new("+=", "PLUS_EQUALS");
  MINUS_EQUALS : punctuation_type.new("-=", "MINUS_EQUALS");
  ASTERISK_EQUALS : punctuation_type.new("*=", "ASTERISK_EQUALS");

  MINUS_MINUS_MINUS : punctuation_type.new("---", "MINUS_MINUS_MINUS");

  -- For meta-grammar
  COLON_COLON_EQUALS : punctuation_type.new("::=", "COLON_COLON_EQUALS");
}

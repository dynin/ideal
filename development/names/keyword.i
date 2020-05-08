-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Keywords used in the ideal system.
--- TODO: convert into namespace, drop static.
class keyword {
  extends deeply_immutable data;

  -- TODO: move out of keywords
  static FOR_NAME : simple_name.make("for");

  static THIS : base_token_type.new("this");
  static SUPER : base_token_type.new("super");
  static NEW : base_token_type.new("new");

  static OR : base_token_type.new("or", base_symbols.OR);

  static AS : base_token_type.new("as", base_symbols.AS);
  static IS : base_token_type.new("is", base_symbols.IS);
  static IS_NOT : base_token_type.new("is_not", base_symbols.IS_NOT);

  static ASSERT : base_token_type.new("assert", base_symbols.ASSERT);

  static RETURN : base_token_type.new("return", base_symbols.RETURN);
  static IF : base_token_type.new("if", base_symbols.IF);
  static ELSE : base_token_type.new("else", base_symbols.ELSE);

  static LOOP : base_token_type.new("loop", base_symbols.LOOP);

  static WHILE : base_token_type.new("while", base_symbols.WHILE);
  static FOR : base_token_type.new("for", base_symbols.FOR);
  static IMPORT : base_token_type.new("import", base_symbols.IMPORT);
  static USE : base_token_type.new("use", base_symbols.USE);
  static TARGET : base_token_type.new("target", base_symbols.TARGET);

  static PLEASE : base_token_type.new("please", base_symbols.PLEASE);
}

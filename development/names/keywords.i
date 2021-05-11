-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Keywords used in the ideal system.
namespace keywords {
  RESERVED : keyword.new("reserved");

  THIS : keyword.new("this");
  SUPER : keyword.new("super");
  NEW : keyword.new("new");

  OR : keyword.new("or", base_symbols.OR);

  IS : keyword.new("is", base_symbols.IS);
  IS_NOT : keyword.new("is_not", base_symbols.IS_NOT);

  RETURN : keyword.new("return", base_symbols.RETURN);
  IF : keyword.new("if", base_symbols.IF);
  ELSE : keyword.new("else", base_symbols.ELSE);

  LOOP : keyword.new("loop", base_symbols.LOOP);

  WHILE : keyword.new("while", base_symbols.WHILE);
  FOR : keyword.new("for", base_symbols.FOR);
  IMPORT : keyword.new("import", base_symbols.IMPORT);
  USE : keyword.new("use", base_symbols.USE);
  TARGET : keyword.new("target", base_symbols.TARGET);

  PLEASE : keyword.new("please", base_symbols.PLEASE);
}

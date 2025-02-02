-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Keywords used in the ideal system.
namespace keywords {
  RESERVED : keyword.new("reserved");

  THIS : keyword.new("this");
  SUPER : keyword.new("super");
  NEW : keyword.new("new");

  OR : keyword.new("or");

  IS : keyword.new("is");
  IS_NOT : keyword.new("is_not");

  RETURN : keyword.new("return");
  IF : keyword.new("if");
  ELSE : keyword.new("else");

  LOOP : keyword.new("loop");

  WHILE : keyword.new("while");
  FOR : keyword.new("for");
  IMPORT : keyword.new("import");

  SWITCH : keyword.new("switch");
  CASE : keyword.new("case");
  DEFAULT : keyword.new("default");

  USE : keyword.new("use");
  TARGET : keyword.new("target");

  PLEASE : keyword.new("please");

  -- For meta-grammar
  GRAMMAR : keyword.new("grammar");
  TERMINAL : keyword.new("terminal");
  NONTERMINAL : keyword.new("nonterminal");
}

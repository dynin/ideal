-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Constructs are the abstract syntax tree (AST) used in ideal.
interface construct {
  extends data, position;

  readonly list[construct] children();
  position source_position();
}

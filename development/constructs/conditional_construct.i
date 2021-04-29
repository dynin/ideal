-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

meta_construct class conditional_construct {
  construct cond_expr;
  construct then_expr;
  construct or null else_expr;
  boolean is_statement;
}

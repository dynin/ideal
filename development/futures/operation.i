-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An operation (a.k.a. procedure or callback).
interface operation {
  implements data, stringable;

  --- Schedule this operation for execution.
  void schedule();
}
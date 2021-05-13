-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

enum type_format {
  implements deeply_immutable data, reference_equality, stringable;

  SHORT;
  ONE_PARENT;
  TWO_PARENTS;
  FULL;
}

-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A value that provides a natural equivalence relation.
interface has_equivalence {
  subtypes equality_comparable;

  equivalence_relation[readonly has_equivalence] equivalence;
}

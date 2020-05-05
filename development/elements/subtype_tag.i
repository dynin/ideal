-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A subtype tag, such as |subtypes|, |extends| and |implements|.
interface subtype_tag {
  extends deeply_immutable data, reference_equality, stringable;

  simple_name name;
}

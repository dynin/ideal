-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

enum documentation_section {
  implements deeply_immutable data;
  -- TODO: all enums should be equality_comparable and stringable.
  subtypes reference_equality, stringable;

  ALL;
  SUMMARY;
  -- DESCRIPTION;
  -- COPYRIGHT;
  -- LICENSE;
}

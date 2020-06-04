-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A category associated with a variable, such as instance field, static field,
--- loval variable, or an wnum value declaration.
enum variable_category {
  extends deeply_immutable data;

  LOCAL;
  INSTANCE;
  STATIC;
  ENUM_VALUE;
}

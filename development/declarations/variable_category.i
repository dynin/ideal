-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A category associated with a variable, such as instance field, static field,
--- loval variable, or an wnum value declaration.
enum variable_category {
  LOCAL;
  INSTANCE;
  STATIC;
  ENUM_VALUE;
}

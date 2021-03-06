-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A category of a given procedure: method, constructor, or static method.
enum procedure_category {
  extends deeply_immutable data;

  CONSTRUCTOR;
  METHOD;
  STATIC;
}

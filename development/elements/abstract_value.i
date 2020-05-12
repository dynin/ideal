-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Abstract value denotes a set of values used in abstract interpretation.
--- It can either be a value (e.g. |42|) or a type (e.g. |integer|).
interface abstract_value  {
  extends readonly data, stringable;

  type type_bound;
  action to_action(origin the_origin) pure;
}

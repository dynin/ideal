-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Abstract value denotes a set of values used in abstract interpretation.
--- It can either be a value (e.g. |42|) or a type (e.g. |integer|).
interface abstract_value {
  extends readonly data, stringable;

  type type_bound;
  boolean is_parametrizable;
  action to_action(origin the_origin) pure;
}

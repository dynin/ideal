-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A context maintained for the specialization operation,
--- such as getting an instance of a parameterized types
--- for given parameter bindings.
interface specialization_context {
  extends deeply_immutable data, stringable;

  abstract_value or null lookup(principal_type key) pure;
}

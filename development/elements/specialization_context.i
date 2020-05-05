-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A context maintained for the specialization operation,
--- such as getting an instance of a parameterized types
--- for given parameter bindings.
interface specialization_context {
  extends deeply_immutable data, stringable;

  abstract_value or null lookup(principal_type key);
}

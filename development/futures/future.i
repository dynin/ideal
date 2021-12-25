-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A |future| encapsulates the result of a delayed computation.
interface future[covariant value element] {
  boolean is_done;
  element or null value;
  observe(operation observer, lifespan the_lifespan);
}

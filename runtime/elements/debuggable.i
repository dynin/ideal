-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.adapters.java.lang.String;
import ideal.machine.adapters.java.lang.Object;

--- Bridges string conversion between ideal and Objects.
class debuggable {
  extends Object;
  implements stringable;

  -- TODO: autogenerate trivial constructor.
  protected debuggable() { }

  override string to_string() {
    return utilities.describe(this);
  }

  override final String toString() {
    return utilities.s(to_string);
  }
}

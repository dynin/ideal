-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declaration pass for types.
enum declaration_pass {
  implements readonly displayable;

  NONE;
  FLAVOR_PROFILE;
  TYPES_AND_PROMOTIONS;
  METHODS_AND_VARIABLES;

  boolean is_before(declaration_pass other) {
    return this.ordinal < other.ordinal;
  }

  boolean is_after(declaration_pass other) {
    return this.ordinal > other.ordinal;
  }

  override string display() => to_string;

  -- TODO: static declaration_pass last() => values()[values().length - 1];
  static declaration_pass last => declaration_pass.METHODS_AND_VARIABLES;
}

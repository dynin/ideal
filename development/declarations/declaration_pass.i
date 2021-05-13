-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Declaration pass for types.
enum declaration_pass {
  implements deeply_immutable data, stringable, reference_equality, readonly displayable;

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

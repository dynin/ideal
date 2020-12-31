-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Variance modifiers, such as covariant/contravariant and the (trademarked) combivariant.
-- TODO: use enum here?
class variance_modifier {
  extends base_modifier_kind;

  static variance_modifier invariant_modifier : variance_modifier.new("invariant");
  static variance_modifier covariant_modifier : variance_modifier.new("covariant");
  static variance_modifier contravariant_modifier : variance_modifier.new("contravariant");
  static variance_modifier combivariant_modifier : variance_modifier.new("combivariant");

  private variance_modifier(string name) {
    super(name);
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Access modifiers, such as private/public.
-- TODO: make an enum?
class access_modifier {
  extends base_modifier_kind;

  static access_modifier public_modifier : access_modifier.new("public");
  static access_modifier private_modifier : access_modifier.new("private");
  static access_modifier protected_modifier : access_modifier.new("protected");
  static access_modifier local_modifier : access_modifier.new("local");

  private access_modifier(string name) {
    super(name);
  }
}

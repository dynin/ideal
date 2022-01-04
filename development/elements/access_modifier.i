-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.runtime.elements.debuggable;
implicit import ideal.runtime.logs;

--- An access level modifier, such as "public" or "private".
-- TODO: turn into an enum.
class access_modifier {
  implements modifier_kind, readonly displayable;
  extends debuggable;

  static public_modifier : access_modifier.new("public");
  static private_modifier : access_modifier.new("private");
  static protected_modifier : access_modifier.new("protected");
  static local_modifier : access_modifier.new("local");

  private simple_name the_name;

  private access_modifier(string name) {
    this.the_name = simple_name.make(name);
  }

  override simple_name name => the_name;

  -- TODO: share code with name_utilities.in_brackets
  override string to_string => "<" ++ the_name.to_string() ++ ">";

  override string display => to_string();
}

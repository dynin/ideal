-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Base class for all modifiers.
class base_modifier_kind {

  extends debuggable;
  implements modifier_kind, readonly displayable;

  private final simple_name the_name;

  base_modifier_kind(string name) {
    this.the_name = simple_name.make(name);
  }

  override simple_name name => the_name;

  override string to_string => name_utilities.in_brackets(the_name);

  override string display() => to_string();
}

-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Subtype tag implementations, such as "extends" etc.
class base_subtype_tag {
  extends debuggable;
  implements subtype_tag, readonly displayable;

  private final simple_name the_name;

  base_subtype_tag(string the_name) {
    this.the_name = simple_name.make(the_name);
  }

  simple_name name => the_name;

  string to_string => name_utilities.in_brackets(the_name);

  string display => to_string();
}

-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of type kind.
class base_kind {
  extends debuggable;
  implements kind, readonly displayable;

  private final simple_name the_name;
  private final flavor_profile the_default_profile;

  base_kind(string the_name, flavor_profile the_default_profile) {
    this.the_name = simple_name.make(the_name);
    this.the_default_profile = the_default_profile;
  }

  override simple_name name => the_name;

  override flavor_profile default_profile => the_default_profile;

  override boolean is_namespace => the_default_profile == flavor_profiles.nameonly_profile;

  override string to_string => the_name.to_string();

  override string display => to_string();
}

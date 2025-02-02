-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of type kind.
class base_kind {
  extends debuggable;
  implements kind, readonly displayable;

  private simple_name the_name;
  private flavor_profile the_default_profile;
  private boolean does_support_constructors;

  base_kind(string the_name, flavor_profile the_default_profile, boolean does_support_constructors) {
    this.the_name = simple_name.make(the_name);
    this.the_default_profile = the_default_profile;
    this.does_support_constructors = does_support_constructors;
  }

  override simple_name name => the_name;

  override flavor_profile default_profile => the_default_profile;

  override boolean is_namespace => the_default_profile == flavor_profiles.nameonly_profile;

  override boolean supports_constructors => does_support_constructors;

  override string to_string => the_name.to_string();

  override string display => to_string();
}

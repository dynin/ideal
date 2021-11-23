-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of a flavor.
class type_flavor_impl {
  extends debuggable;
  implements type_flavor, readonly displayable;

  private final simple_name the_name;
  private final flavor_profile profile;
  -- TODO: deeply immutable
  private immutable list[type_flavor] superflavors;
  types : hash_dictionary[principal_type, type].new();

  type_flavor_impl(string the_name, flavor_profile profile,
      readonly list[type_flavor] declared_superflavors) {
    this.the_name = simple_name.make(the_name);
    this.profile = profile;
    this.superflavors = declared_superflavors.frozen_copy;
  }

  override simple_name name => the_name;

  override flavor_profile get_profile => profile;

  override immutable list[type_flavor] get_superflavors => superflavors;

  override string to_string => the_name.to_string();

  override string display => to_string();
}

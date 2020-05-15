-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_flavor_profile {
  extends debuggable;
  implements flavor_profile, readonly displayable;

  private final string name;
  private final function[type_flavor, type_flavor] flavor_map;
  private var immutable list[type_flavor] or null cached_flavors;

  base_flavor_profile(string name, function[type_flavor, type_flavor] flavor_map) {
    this.name = name;
    this.flavor_map = flavor_map;
  }

  type_flavor map(type_flavor from) => flavor_map(from);

  override type_flavor default_flavor() {
    return map(flavor.DEFAULT_FLAVOR);
  }

  override boolean supports(type_flavor flavor) {
    return map(flavor) == flavor;
  }

  override immutable list[type_flavor] supported_flavors() {
    result : cached_flavors;
    -- TODO: streamline this with a cached value extension.
    if (result is null) {
      filtered_flavors : base_list[type_flavor].new();
      primary_flavors : flavor.PRIMARY_FLAVORS;
--      for (the_flavor : flavor.PRIMARY_FLAVORS) {
      for (var nonnegative i : 0; i < primary_flavors.size; i += 1) {
        the_flavor : primary_flavors[i];
        if (this.supports(the_flavor)) {
          filtered_flavors.append(the_flavor);
        }
      }
      new_result : filtered_flavors.frozen_copy();
      cached_flavors = new_result;
      return new_result;
    } else {
      return result;
    }
  }

  override string to_string => name;

  override string display => name;
}

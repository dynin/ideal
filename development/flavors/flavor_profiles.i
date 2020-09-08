-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace flavor_profiles {

  private type_flavor nameonly_map(type_flavor from) pure => flavor.nameonly_flavor;

  nameonly_profile : base_flavor_profile.new("nameonly_profile", nameonly_map);

  private type_flavor mutable_map(type_flavor from) pure => from;

  mutable_profile : base_flavor_profile.new("mutable_profile", mutable_map);

  private type_flavor shallow_mutable_map(type_flavor from) pure {
    if (from == flavor.immutable_flavor) {
      return flavor.deeply_immutable_flavor;
    } else {
      return from;
    }
  }

  shallow_mutable_profile : base_flavor_profile.new("shallow_mutable_profile",
      shallow_mutable_map);

  private type_flavor immutable_map(type_flavor from) pure {
    if (from == flavor.writeonly_flavor ||
        from == flavor.mutable_flavor ||
        from == flavor.readonly_flavor ||
        from == flavor.any_flavor) {
      return flavor.immutable_flavor;
    } else {
      return from;
    }
  }

  immutable_profile : base_flavor_profile.new("immutable_profile", immutable_map);

  private type_flavor deeply_immutable_map(type_flavor from) pure {
    if (from == flavor.any_flavor ||
        from == flavor.readonly_flavor ||
        from == flavor.writeonly_flavor ||
        from == flavor.mutable_flavor ||
        from == flavor.immutable_flavor ||
        from == flavor.deeply_immutable_flavor) {
      return flavor.deeply_immutable_flavor;
    } else if (from == flavor.nameonly_flavor || from == flavor.raw_flavor) {
      return from;
    } else {
      utilities.panic("Unknown flavor: " ++ from);
    }
  }

  deeply_immutable_profile :
      base_flavor_profile.new("deeply_immutable_profile", deeply_immutable_map);

  flavor_profile combine(flavor_profile or null flavors1, flavor_profile or null flavors2) {
    if (flavors1 is null) {
      assert flavors2 is_not null;
      return flavors2;
    }

    if (flavors2 is null) {
      return flavors1;
    }

    if (flavors1 == nameonly_profile || flavors2 == nameonly_profile) {
      return nameonly_profile;
    }

    if (flavors1 == deeply_immutable_profile || flavors2 == deeply_immutable_profile) {
      return deeply_immutable_profile;
    }

    if (flavors1 == immutable_profile || flavors2 == immutable_profile) {
      return immutable_profile;
    }

    if (flavors1 == shallow_mutable_profile || flavors2 == shallow_mutable_profile) {
      return shallow_mutable_profile;
    }

    assert flavors1 == mutable_profile && flavors2 == mutable_profile;
    return mutable_profile;
  }
}

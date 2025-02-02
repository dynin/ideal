-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

namespace flavor {
  type_flavor nameonly_flavor : type_flavor_impl.new("nameonly", flavor_profiles.nameonly_profile,
      empty[type_flavor].new());

  type_flavor any_flavor : type_flavor_impl.new("any", flavor_profiles.mutable_profile,
      empty[type_flavor].new());

  type_flavor readonly_flavor : type_flavor_impl.new("readonly", flavor_profiles.mutable_profile,
      [any_flavor, ]);

  type_flavor writeonly_flavor : type_flavor_impl.new("writeonly", flavor_profiles.mutable_profile,
      [any_flavor, ]);

  type_flavor mutable_flavor : type_flavor_impl.new("mutable", flavor_profiles.mutable_profile,
      [readonly_flavor, writeonly_flavor]);

  type_flavor immutable_flavor : type_flavor_impl.new("immutable",
      flavor_profiles.immutable_profile, [readonly_flavor, ]);

  type_flavor deeply_immutable_flavor : type_flavor_impl.new("deeply_immutable",
      flavor_profiles.deeply_immutable_profile, [immutable_flavor, ]);

  -- TODO: raw flavor should be special, allowing write access to final (non-var) fields.
  -- Constructor procedures should verify that all invariants are satisfied.
  -- For now, just allow promotions from raw flavor to mutable flavor.
  type_flavor raw_flavor : type_flavor_impl.new("raw", flavor_profiles.mutable_profile,
      [mutable_flavor, ]);

  all_flavors : [
    nameonly_flavor, any_flavor, readonly_flavor, writeonly_flavor,
    mutable_flavor, raw_flavor, immutable_flavor, deeply_immutable_flavor
  ];

  DEFAULT_FLAVOR : mutable_flavor;

  PRIMARY_FLAVORS : [
    any_flavor, readonly_flavor, writeonly_flavor, mutable_flavor,
    raw_flavor, immutable_flavor, deeply_immutable_flavor
  ];
}

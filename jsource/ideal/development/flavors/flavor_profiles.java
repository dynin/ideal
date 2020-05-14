/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.flavors;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

public class flavor_profiles {

  public static flavor_profile nameonly_profile = new base_flavor_profile("nameonly_profile") {
    @Override
    public type_flavor map(type_flavor from) {
      return flavor.nameonly_flavor;
    }
  };

  public static flavor_profile mutable_profile = new base_flavor_profile("mutable_profile") {
    @Override
    public type_flavor map(type_flavor from) {
      return from;
    }
  };

  public static flavor_profile shallow_mutable_profile =
      new base_flavor_profile("shallow_mutable_profile") {
    @Override
    public type_flavor map(type_flavor from) {
      if (from == flavor.immutable_flavor) {
        return flavor.deeply_immutable_flavor;
      } else {
        return from;
      }
    }
  };

  public static flavor_profile immutable_profile = new base_flavor_profile("immutable_profile") {
    @Override
    public type_flavor map(type_flavor from) {
      if (from == flavor.writeonly_flavor ||
          from == flavor.mutable_flavor ||
          from == flavor.readonly_flavor ||
          from == flavor.any_flavor) {
        return flavor.immutable_flavor;
      } else {
        return from;
      }
    }
  };

  public static flavor_profile deeply_immutable_profile =
      new base_flavor_profile("deeply_immutable_profile") {
    @Override
    public type_flavor map(type_flavor from) {
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
        utilities.panic("Unknown flavor: " + from);
        return null;
      }
    }
  };

  public static flavor_profile combine(flavor_profile flavors1, flavor_profile flavors2) {
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

/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
      return flavors.nameonly_flavor;
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
      if (from == flavors.immutable_flavor) {
        return flavors.deeply_immutable_flavor;
      } else {
        return from;
      }
    }
  };

  public static flavor_profile immutable_profile = new base_flavor_profile("immutable_profile") {
    @Override
    public type_flavor map(type_flavor from) {
      if (from == flavors.writeonly_flavor ||
          from == flavors.mutable_flavor ||
          from == flavors.readonly_flavor ||
          from == flavors.any_flavor) {
        return flavors.immutable_flavor;
      } else {
        return from;
      }
    }
  };

  public static flavor_profile deeply_immutable_profile =
      new base_flavor_profile("deeply_immutable_profile") {
    @Override
    public type_flavor map(type_flavor from) {
      if (from == flavors.any_flavor ||
          from == flavors.readonly_flavor ||
          from == flavors.writeonly_flavor ||
          from == flavors.mutable_flavor ||
          from == flavors.immutable_flavor ||
          from == flavors.deeply_immutable_flavor) {
        return flavors.deeply_immutable_flavor;
      } else if (from == flavors.nameonly_flavor || from == flavors.raw_flavor) {
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

/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.flavors;

import ideal.development.elements.*;
import static ideal.development.flavors.flavor_profiles.*;

public class flavors {
  public static final type_flavor nameonly_flavor =
      new type_flavor_impl("nameonly", nameonly_profile);

  public static final type_flavor any_flavor =
      new type_flavor_impl("any", mutable_profile);

  public static final type_flavor readonly_flavor =
      new type_flavor_impl("readonly", mutable_profile, any_flavor);

  public static final type_flavor writeonly_flavor =
      new type_flavor_impl("writeonly", mutable_profile, any_flavor);

  public static final type_flavor mutable_flavor =
      new type_flavor_impl("mutable", mutable_profile, readonly_flavor, writeonly_flavor);

  public static final type_flavor immutable_flavor =
      new type_flavor_impl("immutable", immutable_profile, readonly_flavor);

  public static final type_flavor deeply_immutable_flavor =
      new type_flavor_impl("deeply_immutable", deeply_immutable_profile, immutable_flavor);

  public static final type_flavor raw_flavor =
      new type_flavor_impl("raw", mutable_profile);

  public static final type_flavor[] all_flavors = {
    nameonly_flavor, any_flavor, readonly_flavor, writeonly_flavor,
    mutable_flavor, raw_flavor, immutable_flavor, deeply_immutable_flavor
  };

  public static final type_flavor DEFAULT_FLAVOR = flavors.mutable_flavor;

  public static final type_flavor[] PRIMARY_FLAVORS = {
    any_flavor, readonly_flavor, writeonly_flavor, mutable_flavor,
    immutable_flavor, deeply_immutable_flavor
  };
}

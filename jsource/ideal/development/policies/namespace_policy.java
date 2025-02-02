/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.policies;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.actions.*;
import ideal.development.values.*;

public class namespace_policy extends base_policy {

  public static final namespace_policy instance = new namespace_policy();

  @Override
  public signal declare_type(principal_type new_type, declaration_pass pass,
      action_context context) {

    assert !(new_type instanceof parametrized_type);
    if (new_type.has_flavor_profile()) {
      assert new_type.get_flavor_profile() == flavor_profiles.nameonly_profile;
    } else {
      ((base_principal_type) new_type).set_flavor_profile(flavor_profiles.nameonly_profile);
    }
    return ok_signal.instance;
  }
}

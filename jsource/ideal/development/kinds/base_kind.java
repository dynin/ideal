/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.kinds;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.flavors.*;

public class base_kind extends debuggable implements kind, readonly_displayable {
  private final simple_name name;
  private final flavor_profile default_profile;

  public base_kind(simple_name name, flavor_profile default_profile) {
    this.name = name;
    this.default_profile = default_profile;
  }

  public base_kind(String name, flavor_profile default_profile) {
    this(simple_name.make(name), default_profile);
  }

  @Override
  public simple_name name() {
    return name;
  }

  @Override
  public flavor_profile default_profile() {
    return default_profile;
  }

  @Override
  public boolean is_namespace() {
    return default_profile == flavor_profiles.nameonly_profile;
  }

  @Override
  public string to_string() {
    return name.to_string();
  }

  @Override
  public string display() {
    return to_string();
  }
}

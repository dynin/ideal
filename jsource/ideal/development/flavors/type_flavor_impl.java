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
import ideal.development.names.*;

public class type_flavor_impl extends debuggable implements type_flavor, readonly_displayable {
  public final simple_name name;
  private final flavor_profile profile;
  private immutable_list<type_flavor> superflavors;
  public dictionary<principal_type, type> types = new hash_dictionary<principal_type, type>();

  type_flavor_impl(String name, flavor_profile profile, type_flavor... declared_superflavors) {
    this.name = simple_name.make(name);
    this.profile = profile;
    list<type_flavor> superflavors = new base_list<type_flavor>();
    for (type_flavor superflavor : declared_superflavors) {
      superflavors.append(superflavor);
    }
    this.superflavors = superflavors.frozen_copy();
  }

  @Override
  public simple_name name() {
    return name;
  }

  @Override
  public flavor_profile get_profile() {
    return profile;
  }

  @Override
  public immutable_list<type_flavor> get_superflavors() {
    return superflavors;
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

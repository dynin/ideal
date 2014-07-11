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

import javax.annotation.Nullable;

public abstract class base_flavor_profile extends debuggable implements flavor_profile,
    readonly_displayable {

  private final string name;
  private @Nullable immutable_list<type_flavor> supported_flavors;

  public base_flavor_profile(String name) {
    this.name = new base_string(name);
  }

  public abstract type_flavor map(type_flavor from);

  @Override
  public type_flavor default_flavor() {
    return map(flavors.DEFAULT_FLAVOR);
  }

  @Override
  public boolean supports(type_flavor flavor) {
    return map(flavor) == flavor;
  }

  @Override
  public immutable_list<type_flavor> supported_flavors() {
    if (supported_flavors == null) {
      list<type_flavor> filtered_flavors = new base_list<type_flavor>();
      for (type_flavor flavor : flavors.PRIMARY_FLAVORS) {
        if (this.supports(flavor)) {
          filtered_flavors.append(flavor);
        }
      }
      supported_flavors = filtered_flavors.frozen_copy();
    }
    return supported_flavors;
  }

  @Override
  public string display() {
    return name;
  }

  @Override
  public string to_string() {
    return name;
  }
}

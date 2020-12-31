/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.flavors.*;

public abstract class base_type extends debuggable implements type {

  @Override
  public type type_bound() {
    return this;
  }

  @Override
  public action to_action(origin pos) {
    return new concrete_type_action(this, pos);
  }

  protected static type do_get_flavored(base_principal_type the_type, type_flavor new_flavor) {
    type_flavor_impl the_flavor = (type_flavor_impl) new_flavor;
    if (the_flavor == flavor.nameonly_flavor) {
      return the_type;
    }

    type flavored = the_flavor.types.get(the_type);

    if (flavored == null) {
      flavored = new flavored_type(the_type, the_flavor);
      the_flavor.types.put(the_type, flavored);
    }

    return flavored;
  }

  protected abstract type_declaration_context get_context();

  public abstract string describe(type_format format);

  @Override
  public boolean is_subtype_of(type the_supertype) {
    return get_context().is_subtype_of(this, the_supertype);
  }
}

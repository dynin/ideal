/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;

class flavored_type extends base_type {
  private final base_principal_type main_type;
  private final type_flavor flavor;

  flavored_type(base_principal_type main_type, type_flavor flavor) {
    this.main_type = main_type;
    this.flavor = flavor;
    if (main_type.get_kind() == type_kinds.union_kind) {
      // TODO: this should never happen.
      utilities.panic("flavored union " + this);
    }
  }

  @Override
  public principal_type principal() {
    return main_type;
  }

  @Override
  public identifier short_name() {
    return main_type.short_name();
  }

  @Override
  public type get_flavored(type_flavor new_flavor) {
    new_flavor = main_type.get_flavor_profile().map(new_flavor);
    if (new_flavor == flavor) {
      return this;
    }
    return do_get_flavored(main_type, new_flavor);
  }

  @Override
  public type_flavor get_flavor() {
    return flavor;
  }

  @Override
  protected type_declaration_context get_context() {
    return main_type.get_context();
  }

  @Override
  public string describe(type_format format) {
    return new base_string(flavor.to_string(), new base_string(" "), main_type.describe(format));
  }

  @Override
  public string to_string() {
    return describe(type_format.FULL);
  }
}

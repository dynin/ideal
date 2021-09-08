-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

abstract class base_type {
  extends debuggable;
  implements type;

  implement type type_bound => this;

  implement action to_action(origin the_origin) {
    return concrete_type_action.new(this, the_origin);
  }

  protected static type do_get_flavored(base_principal_type the_type, type_flavor new_flavor) {
    the_flavor : new_flavor !> type_flavor_impl;
    if (the_flavor == flavor.nameonly_flavor) {
      return the_type;
    }

    var flavored : the_flavor.types.get(the_type);

    if (flavored is null) {
      flavored = flavored_type.new(the_type, the_flavor);
      the_flavor.types.put(the_type, flavored);
    }

    return flavored;
  }

  protected abstract type_declaration_context get_context();

  abstract string describe(type_format format);

  implement boolean is_parametrizable() {
    return get_context().is_parametrizable(this);
  }

  implement boolean is_subtype_of(type the_supertype) {
    return get_context().is_subtype_of(this, the_supertype);
  }
}

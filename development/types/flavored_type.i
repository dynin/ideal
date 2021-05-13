-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class flavored_type {
  extends base_type;

  private final base_principal_type main_type;
  private final type_flavor the_type_flavor;

  flavored_type(base_principal_type main_type, type_flavor the_type_flavor) {
    this.main_type = main_type;
    this.the_type_flavor = the_type_flavor;
    if (main_type.get_kind == type_kinds.union_kind) {
      -- This should never happen.
      utilities.panic("Flavored union " ++ this);
    }
  }

  implement principal_type principal => main_type;

  implement identifier short_name => main_type.short_name;

  implement type get_flavored(var type_flavor new_flavor) {
    new_flavor = main_type.get_flavor_profile.map(new_flavor);
    if (new_flavor == the_type_flavor) {
      return this;
    }
    return do_get_flavored(main_type, new_flavor);
  }

  implement type_flavor get_flavor => the_type_flavor;

  implement protected type_declaration_context get_context => main_type.get_context();

  implement string describe(type_format format) {
    return the_type_flavor.to_string ++ " " ++ main_type.describe(format);
  }

  implement string to_string => describe(type_format.FULL);
}

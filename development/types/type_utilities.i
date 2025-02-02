-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.development.origins.special_origin;

namespace type_utilities {
  origin PRIMARY_TYPE_ORIGIN : special_origin.new("[primary_type]");

  immutable list[simple_name] get_full_names(var principal_type or null the_type) {
    names : base_list[simple_name].new();

    while (the_type is_not null) {
      name : the_type.short_name;
      if (name is simple_name) {
        names.append(name);
      }
      the_type = the_type.get_parent;
    }

    return names.frozen_copy.reversed;
  }

  type make_union(readonly list[abstract_value] parameters) {
    return union_type.make_union(type_parameters.new(parameters));
  }

  boolean is_union(type the_type) {
    return the_type.principal is union_type;
  }

  immutable list[abstract_value] get_union_parameters(type the_type) {
    assert is_union(the_type);
    return (the_type.principal !> union_type).get_parameters.the_list;
  }

  boolean is_type_alias(type the_type) {
    return the_type.principal.get_kind == type_kinds.type_alias_kind;
  }

  -- Note: this can fail in case of circular declarations.
  flavor_profile get_flavor_profile(principal_type the_type) {
    the_principal : the_type !> base_principal_type;

    if (!the_principal.has_flavor_profile) {
      if (!the_principal.get_pass().is_before(declaration_pass.FLAVOR_PROFILE)) {
        utilities.panic("P " ++ the_principal.get_pass() ++ " of " ++ the_principal);
      }
      assert the_principal.get_pass().is_before(declaration_pass.FLAVOR_PROFILE);
      the_principal.process_declaration(declaration_pass.FLAVOR_PROFILE);
    }

    return the_principal.get_flavor_profile;
  }

  prepare(abstract_value the_value, declaration_pass pass) {
    (the_value.type_bound.principal !> base_principal_type).process_declaration(pass);
  }
}

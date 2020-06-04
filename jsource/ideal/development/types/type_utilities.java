/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.declarations.*;

public class type_utilities {

  // do not instantiate
  private type_utilities() { }

  public static immutable_list<simple_name> get_full_names(@Nullable principal_type the_type) {
    list<simple_name> names = new base_list<simple_name>();

    for (@Nullable principal_type t = the_type; t != null; t = t.get_parent()) {
      if (t.short_name() instanceof simple_name) {
        names.append((simple_name) t.short_name());
      }
    }

    return names.frozen_copy().reverse();
  }

  public static type make_union(readonly_list<abstract_value> parameters) {
    return core_types.union_master_type().bind_parameters(new type_parameters(parameters));
  }

  public static boolean is_union(type the_type) {
    return the_type.principal().get_kind() == type_kinds.union_kind;
  }

  public static immutable_list<abstract_value> get_union_parameters(type the_type) {
    assert is_union(the_type);
    return ((parametrized_type) the_type.principal()).get_parameters().fixed_size_list();
  }

  public static boolean is_type_alias(type the_type) {
    return the_type.principal().get_kind() == type_kinds.type_alias_kind;
  }

  // Note: this can fail in case of circular declarations.
  public static flavor_profile get_flavor_profile(principal_type the_type) {
    base_principal_type the_principal = (base_principal_type) the_type;

    if (!the_principal.has_flavor_profile()) {
      if (!the_principal.get_pass().is_before(declaration_pass.TYPES_AND_PROMOTIONS)) {
        utilities.panic("P " + the_principal.get_pass() + " of " + the_principal);
      }
      assert the_principal.get_pass().is_before(declaration_pass.TYPES_AND_PROMOTIONS);
      the_principal.process_declaration(declaration_pass.TYPES_AND_PROMOTIONS);
    }

    return the_principal.get_flavor_profile();
  }

  public static void prepare(abstract_value the_value, declaration_pass pass) {
    ((base_principal_type) the_value.type_bound().principal()).process_declaration(pass);
  }
}

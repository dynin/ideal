-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class union_type extends base_principal_type {
  private static special_name union_name = new special_name(new base_string("union"));
  private static dictionary<type_parameters, union_type> cached_types =
      new hash_dictionary<type_parameters, union_type>();

  private static @Nullable type_declaration_context the_context;

  private type_parameters parameters;

  private union_type(type_parameters parameters) {
    super(null, declaration_pass.NONE, null);
    this.parameters = parameters;
  }

  public static union_type make_union(type_parameters parameters) {
    assert parameters != null;

    @Nullable union_type result = cached_types.get(parameters);
    if (result == null) {
      result = new union_type(parameters);
      cached_types.put(parameters, result);
    }

    return result;
  }

  public type_parameters get_parameters() {
    return parameters;
  }

  @Override
  public kind get_kind() {
    return type_kinds.union_kind;
  }

  @Override
  public action_name short_name() {
    return union_name;
  }

  @Override
  public @Nullable principal_type get_parent() {
    return null;
  }

  @Override
  public type get_flavored(type_flavor flavor) {
    immutable_list<abstract_value> parameters = get_parameters().fixed_size_list();
    list<abstract_value> new_parameters = new base_list<abstract_value>();
    for (int i = 0; i < parameters.size(); ++i) {
      abstract_value the_parameter = parameters.get(i);
      if (the_parameter instanceof type) {
        new_parameters.append(((type) the_parameter).get_flavored(flavor));
      } else {
        new_parameters.append(the_parameter);
      }
    }
    return make_union(new type_parameters(new_parameters));
  }

  @Override
  public flavor_profile get_flavor_profile() {
    if (the_flavor_profile == null) {
      the_flavor_profile = default_flavor_profile();
    }
    return the_flavor_profile;
  }

  @Override
  public flavor_profile default_flavor_profile() {
    immutable_list<abstract_value> parameters = get_parameters().fixed_size_list();
    flavor_profile result = flavor_profiles.mutable_profile;

    for (int i = 0; i < parameters.size(); ++i) {
      flavor_profile profile = type_utilities.get_flavor_profile(
          parameters.get(i).type_bound().principal());
      result = flavor_profiles.combine(result, profile);
    }

    return result;
  }

  @Override
  protected void do_declare_actual(declaration_pass pass) {
    immutable_list<abstract_value> parameters = get_parameters().fixed_size_list();
    for (int i = 0; i < parameters.size(); ++i) {
      abstract_value the_parameter = parameters.get(i);
      type_utilities.prepare(the_parameter, pass);
    }
  }

  @Override
  protected type_declaration_context get_context() {
    return the_context;
  }

  public static void set_context(type_declaration_context the_context) {
    union_type.the_context = the_context;
  }

  @Override
  public string describe(type_format format) {
    if (format == type_format.FULL) {
      return new base_string(union_name.to_string(), parameters.to_string());
    } else {
      return new base_string(union_name.to_string(), "[...]");
    }
  }
}

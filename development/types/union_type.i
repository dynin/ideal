-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class union_type {
  extends base_principal_type;

  private static special_name union_name : special_name.new("union");
  private static dictionary[type_parameters, union_type] cached_types :
      hash_dictionary[type_parameters, union_type].new();
  private static var type_declaration_context or null the_context;

  private type_parameters parameters;

  private union_type(type_parameters parameters) {
    super(missing.instance, declaration_pass.NONE, missing.instance);
    this.parameters = parameters;
  }

  static union_type make_union(type_parameters parameters) {
    verify parameters is_not null;

    var result : cached_types.get(parameters);
    if (result is null) {
      result = union_type.new(parameters);
      cached_types.put(parameters, result);
    }

    return result;
  }

  type_parameters get_parameters => parameters;

  implement kind get_kind => type_kinds.union_kind;

  implement action_name short_name => union_name;

  implement principal_type or null get_parent => missing.instance;

  implement type get_flavored(type_flavor flavor) {
    new_parameters : base_list[abstract_value].new();

    -- TODO: optimize when parameters don't change?
    for (the_parameter : parameters.fixed_size_list()) {
      if (the_parameter is type) {
        new_parameters.append(the_parameter.get_flavored(flavor));
      } else {
        new_parameters.append(the_parameter);
      }
    }

    return make_union(type_parameters.new(new_parameters));
  }

  implement flavor_profile get_flavor_profile() {
    -- TODO: drop variable
    var result : the_flavor_profile;
    if (result is null) {
      result = default_flavor_profile();
      the_flavor_profile = result;
    }
    return result;
  }

  implement flavor_profile default_flavor_profile() {
    var flavor_profile result : flavor_profiles.mutable_profile;

    for (the_parameter : parameters.fixed_size_list()) {
      profile : type_utilities.get_flavor_profile(the_parameter.type_bound.principal);
      result = flavor_profiles.combine(result, profile);
    }

    return result;
  }

  implement protected void do_declare_actual(declaration_pass pass) {
    for (the_parameter : parameters.fixed_size_list()) {
      type_utilities.prepare(the_parameter, pass);
    }
  }

  implement protected type_declaration_context get_context() {
    assert the_context is type_declaration_context;
    return the_context;
  }

  static void set_context(type_declaration_context the_context) {
    assert union_type.the_context is null;
    union_type.the_context = the_context;
  }

  implement string describe(type_format format) {
    if (format == type_format.FULL) {
      return union_name ++ parameters;
    } else {
      return union_name ++ "[...]";
    }
  }
}

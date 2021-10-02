-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class parametrized_type {
  extends base_principal_type;

  private master_type master;
  private dont_display var type_parameters or null parameters;

  parametrized_type(master_type master) {
    super(missing.instance, declaration_pass.NONE, missing.instance);
    this.master = master;
  }

  var master_type get_master => master;

  boolean parameters_defined => parameters is_not null;

  var type_parameters get_parameters() {
    assert parameters is_not null;
    return parameters;
  }

  implement kind get_kind => master.get_kind;

  implement flavor_profile default_flavor_profile() {
    if (master.has_flavor_profile) {
      return master.get_flavor_profile;
    } else {
      return master.default_flavor_profile();
    }
  }

  implement action_name short_name => master.short_name();

  implement principal_type or null get_parent() => master.get_parent;

  implement protected type_declaration_context get_context() => master.get_context();

  void set_parameters(type_parameters parameters) {
    assert this.parameters is null;
    if (parameters.the_list.is_empty) {
      utilities.panic("Attempt to parametrize " ++ this ++ " with empty parameters");
    }
    assert parameters.the_list.is_not_empty;  -- TODO: enforce this in the analyzer
    this.parameters = parameters;
  }

  private string parameter_names() {
    if (parameters is_not null) {
      return parameters.to_string();
    } else {
      return "[..unknown..]";
    }
  }

  implement string describe(type_format format) {
    if (format == type_format.FULL) {
      return master.describe(format) ++ parameter_names();
    } else {
      return master.describe(format) ++ "[...]";
    }
  }
}

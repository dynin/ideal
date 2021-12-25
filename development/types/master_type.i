-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class master_type {
  extends base_principal_type;

  private action_name the_name;
  private principal_type or null parent;
  private kind the_kind;
  private dont_display var type_declaration_context or null the_context;
  private var parametrizable_state or null the_parametrizable_state;

  overload master_type(kind the_kind, flavor_profile or null the_flavor_profile,
      action_name the_name, principal_type parent, type_declaration_context the_context,
      declaration or null the_declaration) {
    super(the_flavor_profile, declaration_pass.NONE, the_declaration);
    this.the_name = the_name;
    this.the_kind = the_kind;
    this.the_context = the_context;
    this.parent = parent;
  }

  --- Constructor for simple types.  See |common_types|.
  overload master_type(action_name the_name, kind the_kind) {
    super(the_kind.default_profile, declaration_pass.METHODS_AND_VARIABLES, missing.instance);
    this.the_name = the_name;
    this.the_kind = the_kind;
    this.the_context = missing.instance;
    this.parent = missing.instance;
  }

  implement principal_type or null get_parent => parent;

  implement kind get_kind => the_kind;

  implement flavor_profile default_flavor_profile => the_kind.default_profile;

  implement action_name short_name => the_name;

  implement protected type_declaration_context declaration_context() {
    assert the_context is_not null;
    return the_context;
  }

  protected set_context(type_declaration_context the_context) {
    assert this.the_context is null;
    this.the_context = the_context;
  }

  boolean has_parametrizable_state => the_parametrizable_state is_not null;

  make_parametrizable() {
    assert the_parametrizable_state is null;
    the_parametrizable_state = parametrizable_state.new(this);
  }

  parametrizable_state get_parametrizable() {
    assert the_parametrizable_state is_not null;
    return the_parametrizable_state;
  }

  type bind_parameters(type_parameters parameters) {
    the_state : the_parametrizable_state;
    assert the_state is_not null;
    return the_state.bind_parameters(parameters);
  }

  implement string describe(type_format format) {
    the_parent : parent !> base_type;
    if (the_parent is_not null) {
      if (format == type_format.FULL) {
        return the_parent.describe(type_format.FULL) ++ "." ++ the_name;
      } else if (format == type_format.TWO_PARENTS) {
        return the_parent.describe(type_format.ONE_PARENT) ++ "." ++ the_name;
      } else if (format == type_format.ONE_PARENT) {
        return the_parent.describe(type_format.SHORT) ++ "." ++ the_name;
      }
    }
    return the_name.to_string;
  }
}

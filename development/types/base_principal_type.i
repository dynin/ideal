-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

abstract class base_principal_type {
  extends base_type;
  implements principal_type;

  protected var flavor_profile or null the_flavor_profile;
  protected var declaration_pass last_pass;
  dont_display var private declaration or null the_declaration;

  protected base_principal_type(flavor_profile or null the_flavor_profile,
      declaration_pass last_pass, declaration or null the_declaration) {
    this.the_flavor_profile = the_flavor_profile;
    this.last_pass = last_pass;
    this.the_declaration = the_declaration;
  }

  implement principal_type principal => this;

  implement type_flavor get_flavor => flavor.nameonly_flavor;

  implement boolean has_flavor_profile => the_flavor_profile is_not null;

  implement flavor_profile get_flavor_profile() {
    if (the_flavor_profile is null) {
      -- TODO: signal error instead of panicing.
      utilities.panic("Unset profile in " ++ this ++ " decl " ++ the_declaration);
    }
    return the_flavor_profile;
  }

  implement type get_flavored(type_flavor flavor) {
    var profile : the_flavor_profile;
    if (profile is null) {
      profile = default_flavor_profile();
      the_flavor_profile = profile;
    }

    return do_get_flavored(this, profile.map(flavor));
  }

  set_flavor_profile(flavor_profile the_flavor_profile) {
    assert this.the_flavor_profile is null;
    for (flavor : flavor.all_flavors) {
      if (!the_flavor_profile.supports(flavor)) {
        if ((flavor !> type_flavor_impl).types.contains_key(this)) {
          utilities.panic("Already used " ++ flavor ++ " of " ++ this);
        }
      }
    }
    this.the_flavor_profile = the_flavor_profile;
  }

  declaration_pass get_pass => last_pass;

  implement final declaration or null get_declaration => the_declaration;

  set_declaration(declaration the_declaration) {
    -- TODO: implement assert message
    assert this.the_declaration is null; -- : "Already declared " + this;
    verify the_declaration is_not null;
    this.the_declaration = the_declaration;
  }

  process_declaration(declaration_pass pass) {
    if (pass.is_before(last_pass) || pass == last_pass) {
      return;
    }

    if (last_pass.is_before(declaration_pass.FLAVOR_PROFILE)) {
      do_declare(declaration_pass.FLAVOR_PROFILE);
    }

    if (pass.is_after(declaration_pass.FLAVOR_PROFILE) &&
        last_pass.is_before(declaration_pass.TYPES_AND_PROMOTIONS)) {
      do_declare(declaration_pass.TYPES_AND_PROMOTIONS);
    }

    if (pass == declaration_pass.METHODS_AND_VARIABLES &&
        last_pass.is_before(declaration_pass.METHODS_AND_VARIABLES)) {
      do_declare(declaration_pass.METHODS_AND_VARIABLES);
    }
  }

  protected final do_declare(declaration_pass pass) {
    assert pass.ordinal == last_pass.ordinal + 1;
    last_pass = pass;
    do_declare_actual(pass);
  }

  protected do_declare_actual(declaration_pass pass) {
    assert pass != declaration_pass.NONE;
    the_context : declaration_context;
    assert the_context is_not null;
    the_context.declare_type(this, pass);
  }

  abstract flavor_profile default_flavor_profile();

  implement final string to_string() {
    return describe(type_format.FULL);
  }
}

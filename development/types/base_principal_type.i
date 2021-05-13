-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

abstract class base_principal_type {
  extends base_type;
  implements principal_type;

  protected flavor_profile or null the_flavor_profile;
  protected declaration_pass last_pass;
  dont_display private declaration or null the_declaration;

  protected base_principal_type(flavor_profile or null the_flavor_profile,
      declaration_pass last_pass, declaration or null the_declaration) {
    this.the_flavor_profile = the_flavor_profile;
    this.last_pass = last_pass;
    this.the_declaration = the_declaration;
  }

  override principal_type principal => this;

  override type_flavor get_flavor => flavor.nameonly_flavor;

  override boolean has_flavor_profile => the_flavor_profile is_not null;

  override flavor_profile get_flavor_profile {
    if (the_flavor_profile is null) {
      -- TODO: signal error instead of panicing.
      utilities.panic("Unset profile in " ++ this ++ " decl " ++ the_declaration);
    }
    result : the_flavor_profile;
    assert result is_not null;
    return result;
  }

  override type get_flavored(type_flavor flavor) {
    if (the_flavor_profile == null) {
      if (get_kind() == type_kinds.procedure_kind || get_kind() == type_kinds.reference_kind) {
        the_flavor_profile = default_flavor_profile();
      } else {
        // We used to panic here
        // utilities.panic("No profile for " + this);
        // TODO: does this ever create a problem?
        the_flavor_profile = default_flavor_profile();
      }
    }
    return do_get_flavored(this, the_flavor_profile.map(flavor));
  }

  void set_flavor_profile(flavor_profile the_flavor_profile) {
    assert this.the_flavor_profile == null;
    readonly_list<type_flavor> all_flavors = flavor.all_flavors;
    for (int i = 0; i < all_flavors.size(); ++i) {
      type_flavor flavor = all_flavors.get(i);
      if (!the_flavor_profile.supports(flavor)) {
        if (((type_flavor_impl) flavor).types.contains_key(this)) {
          utilities.panic("Already used " + flavor + " of " + this);
        }
      }
    }
    this.the_flavor_profile = the_flavor_profile;
  }

  declaration_pass get_pass() {
    return last_pass;
  }

  override
  final @Nullable declaration get_declaration() {
    return the_declaration;
  }

  void set_declaration(declaration the_declaration) {
    assert this.the_declaration == null : "Already declared " + this;
    assert the_declaration != null;
    this.the_declaration = the_declaration;
  }

  void process_declaration(declaration_pass pass) {
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

  protected final void do_declare(declaration_pass pass) {
    assert pass.ordinal() == last_pass.ordinal() + 1;
    last_pass = pass;
    do_declare_actual(pass);
  }

  protected void do_declare_actual(declaration_pass pass) {
    assert pass != declaration_pass.NONE;
    type_declaration_context the_context = get_context();
    assert the_context != null;
    the_context.declare_type(this, pass);
  }

  abstract flavor_profile default_flavor_profile();

  override
  final string to_string() {
    return describe(type_format.FULL);
    // return new base_string(describe(type_format.FULL) + "@" + System.identityHashCode(this));
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.library.graphs.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;

public class master_type extends base_principal_type {
  private final action_name short_name;
  private final @Nullable principal_type parent;
  private final kind the_kind;
  @dont_display
  private type_declaration_context the_context;
  private @Nullable parametrizable_state the_parametrizable_state;

  public master_type(kind the_kind, @Nullable flavor_profile the_flavor_profile,
      action_name short_name, principal_type parent, type_declaration_context the_context,
      @Nullable declaration the_declaration) {
    super(the_flavor_profile, declaration_pass.NONE, the_declaration);
    this.short_name = short_name;
    this.the_kind = the_kind;
    this.the_context = the_context;
    this.parent = parent;
  }

  /** Constructor for simple types.  See |core_types|. */
  public master_type(action_name short_name, kind the_kind) {
    super(the_kind.default_profile(), declaration_pass.METHODS_AND_VARIABLES, null);
    this.short_name = short_name;
    this.the_kind = the_kind;
    this.the_context = null;
    this.parent = null;
  }

  @Override
  public @Nullable principal_type get_parent() {
    return parent;
  }

  @Override
  public kind get_kind() {
    return the_kind;
  }

  @Override
  public flavor_profile default_flavor_profile() {
    return the_kind.default_profile();
  }

  @Override
  public action_name short_name() {
    return short_name;
  }

  @Override
  protected type_declaration_context get_context() {
    assert the_context != null;
    return the_context;
  }

  public void set_context(type_declaration_context the_context) {
    assert this.the_context == null;
    this.the_context = the_context;
  }

  public boolean is_parametrizable() {
    return the_parametrizable_state != null;
  }

  public void make_parametrizable() {
    the_parametrizable_state = new parametrizable_state(this);
  }

  public parametrizable_state get_parametrizable() {
    assert the_parametrizable_state != null;
    return the_parametrizable_state;
  }

  public type bind_parameters(type_parameters parameters) {
    assert the_parametrizable_state != null;
    return the_parametrizable_state.bind_parameters(parameters);
  }

  @Override
  public string describe(type_format format) {
    base_type the_parent = (base_type) parent;
    if (the_parent != null) {
      if (format == type_format.FULL) {
        return new base_string(the_parent.describe(type_format.FULL), ".", short_name.to_string());
      } else if (format == type_format.TWO_PARENTS) {
        return new base_string(the_parent.describe(type_format.ONE_PARENT), ".",
            short_name.to_string());
      } else if (format == type_format.ONE_PARENT) {
        return new base_string(the_parent.describe(type_format.SHORT), ".", short_name.to_string());
      }
    }
    return short_name.to_string();
  }
}

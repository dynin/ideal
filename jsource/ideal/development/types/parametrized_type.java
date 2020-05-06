/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.declarations.*;

public class parametrized_type extends base_principal_type {
  private master_type master;
  @dont_display
  private @Nullable type_parameters parameters;

  parametrized_type(master_type master) {
    super(null, declaration_pass.NONE, null);
    this.master = master;
  }

  public boolean parameters_defined() {
    return parameters != null;
  }

  public type_parameters get_parameters() {
    assert parameters != null;
    return parameters;
  }

  public master_type get_master() {
    return master;
  }

  @Override
  public kind get_kind() {
    return master.get_kind();
  }

  @Override
  public flavor_profile default_flavor_profile() {
    if (master.has_flavor_profile()) {
      return master.get_flavor_profile();
    } else {
      return master.default_flavor_profile();
    }
  }

  @Override
  public action_name short_name() {
    return master.short_name();
  }

  @Override
  public @Nullable principal_type get_parent() {
    return master.get_parent();
  }

  @Override
  protected type_declaration_context get_context() {
    return master.get_context();
  }

  void set_parameters(type_parameters parameters) {
    assert this.parameters == null;
    if (parameters.is_empty()) {
      utilities.panic("Attempt to parametrize " + this);
    }
    assert !parameters.is_empty(); // TODO: enforce this in analyzer
    this.parameters = parameters;
  }

  private string parameter_names() {
    if (parameters != null) {
      return parameters.to_string();
    } else {
      return new base_string("[..unknown..]");
    }
  }

  @Override
  public string describe(type_format format) {
    if (format == type_format.FULL) {
      return new base_string(master.describe(format), parameter_names());
    } else {
      return new base_string(master.describe(format), "[...]");
    }
  }
}

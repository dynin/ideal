// Autogenerated from development/types/parametrized_type.i

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;

import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;

public class parametrized_type extends base_principal_type {
  private final master_type master;
  private @dont_display @Nullable type_parameters parameters;
  public parametrized_type(final master_type master) {
    super(null, declaration_pass.NONE, null);
    this.master = master;
  }
  public master_type get_master() {
    return this.master;
  }
  public boolean parameters_defined() {
    return this.parameters != null;
  }
  public type_parameters get_parameters() {
    assert this.parameters != null;
    return this.parameters;
  }
  public @Override kind get_kind() {
    return this.master.get_kind();
  }
  public @Override flavor_profile default_flavor_profile() {
    if (this.master.has_flavor_profile()) {
      return this.master.get_flavor_profile();
    } else {
      return this.master.default_flavor_profile();
    }
  }
  public @Override action_name short_name() {
    return this.master.short_name();
  }
  public @Override @Nullable principal_type get_parent() {
    return this.master.get_parent();
  }
  protected @Override type_declaration_context get_context() {
    return this.master.get_context();
  }
  public void set_parameters(final type_parameters parameters) {
    assert this.parameters == null;
    if (parameters.is_empty()) {
      utilities.panic(ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(new base_string("Attempt to parametrize "), this), new base_string(" with empty parameters")));
    }
    assert parameters.is_not_empty();
    this.parameters = parameters;
  }
  private string parameter_names() {
    if (this.parameters != null) {
      return this.parameters.to_string();
    } else {
      return new base_string("[..unknown..]");
    }
  }
  public @Override string describe(final type_format format) {
    if (format == type_format.FULL) {
      return ideal.machine.elements.runtime_util.concatenate(this.master.describe(format), this.parameter_names());
    } else {
      return ideal.machine.elements.runtime_util.concatenate(this.master.describe(format), new base_string("[...]"));
    }
  }
}
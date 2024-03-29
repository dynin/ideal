// Autogenerated from development/types/master_type.i

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

public class master_type extends base_principal_type {
  private final action_name the_name;
  private final @Nullable principal_type parent;
  private final kind the_kind;
  private @Nullable @dont_display type_declaration_context the_context;
  private @Nullable parametrizable_state the_parametrizable_state;
  public master_type(final kind the_kind, final @Nullable flavor_profile the_flavor_profile, final action_name the_name, final principal_type parent, final type_declaration_context the_context, final @Nullable declaration the_declaration) {
    super(the_flavor_profile, declaration_pass.NONE, the_declaration);
    this.the_name = the_name;
    this.the_kind = the_kind;
    this.the_context = the_context;
    this.parent = parent;
  }
  public master_type(final action_name the_name, final kind the_kind) {
    super(the_kind.default_profile(), declaration_pass.METHODS_AND_VARIABLES, null);
    this.the_name = the_name;
    this.the_kind = the_kind;
    this.the_context = null;
    this.parent = null;
  }
  public @Override @Nullable principal_type get_parent() {
    return this.parent;
  }
  public @Override kind get_kind() {
    return this.the_kind;
  }
  public @Override flavor_profile default_flavor_profile() {
    return this.the_kind.default_profile();
  }
  public @Override action_name short_name() {
    return this.the_name;
  }
  protected @Override type_declaration_context declaration_context() {
    assert this.the_context != null;
    return this.the_context;
  }
  protected void set_context(final type_declaration_context the_context) {
    assert this.the_context == null;
    this.the_context = the_context;
  }
  public boolean has_parametrizable_state() {
    return this.the_parametrizable_state != null;
  }
  public void make_parametrizable() {
    assert this.the_parametrizable_state == null;
    this.the_parametrizable_state = new parametrizable_state(this);
  }
  public parametrizable_state get_parametrizable() {
    assert this.the_parametrizable_state != null;
    return this.the_parametrizable_state;
  }
  public type bind_parameters(final type_parameters parameters) {
    final @Nullable parametrizable_state the_state = this.the_parametrizable_state;
    assert the_state != null;
    return the_state.bind_parameters(parameters);
  }
  public @Override string describe(final type_format format) {
    final base_type the_parent = (base_type) this.parent;
    if (the_parent != null) {
      if (format == type_format.FULL) {
        return ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(the_parent.describe(type_format.FULL), new base_string(".")), this.the_name);
      } else if (format == type_format.TWO_PARENTS) {
        return ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(the_parent.describe(type_format.ONE_PARENT), new base_string(".")), this.the_name);
      } else if (format == type_format.ONE_PARENT) {
        return ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(the_parent.describe(type_format.SHORT), new base_string(".")), this.the_name);
      }
    }
    return this.the_name.to_string();
  }
}

// Autogenerated from development/kinds/base_kind.i

package ideal.development.kinds;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;

public class base_kind extends debuggable implements kind, readonly_displayable {
  private final simple_name the_name;
  private final flavor_profile the_default_profile;
  private final boolean does_support_constructors;
  public base_kind(final string the_name, final flavor_profile the_default_profile, final boolean does_support_constructors) {
    this.the_name = simple_name.make(the_name);
    this.the_default_profile = the_default_profile;
    this.does_support_constructors = does_support_constructors;
  }
  public @Override simple_name name() {
    return this.the_name;
  }
  public @Override flavor_profile default_profile() {
    return this.the_default_profile;
  }
  public @Override boolean is_namespace() {
    return this.the_default_profile == flavor_profiles.nameonly_profile;
  }
  public @Override boolean supports_constructors() {
    return this.does_support_constructors;
  }
  public @Override string to_string() {
    return this.the_name.to_string();
  }
  public @Override string display() {
    return this.to_string();
  }
}

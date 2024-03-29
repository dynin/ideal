// Autogenerated from development/values/base_value_action.i

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.origins.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.jumps.*;
import ideal.development.notifications.*;
import ideal.development.flags.*;

import javax.annotation.Nullable;

public class base_value_action<value_type extends readonly_entity_wrapper> extends debuggable implements action {
  private final origin the_origin;
  public final value_type the_value;
  public base_value_action(final value_type the_value, final origin the_origin) {
    assert the_origin != null;
    this.the_origin = the_origin;
    this.the_value = the_value;
  }
  public final @Override origin deeper_origin() {
    return this.the_origin;
  }
  public @Override abstract_value result() {
    final readonly_entity_wrapper v = this.the_value;
    return (type) v.type_bound();
  }
  public @Override action to_action() {
    return this;
  }
  public @Override boolean has_side_effects() {
    return false;
  }
  public final @Override action combine(final action from, final origin the_origin) {
    if (this.the_value instanceof procedure_value) {
      return ((procedure_value) this.the_value).bind_this_action(from, the_origin);
    } else {
      return this;
    }
  }
  public @Override entity_wrapper execute(final entity_wrapper from_entity, final execution_context context) {
    if (from_entity instanceof jump_wrapper) {
      return ((jump_wrapper) from_entity);
    }
    return (entity_wrapper) this.the_value;
  }
  public @Override @Nullable declaration get_declaration() {
    return null;
  }
  public @Override string to_string() {
    return utilities.describe(this, this.the_value);
  }
}

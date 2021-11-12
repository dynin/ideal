// Autogenerated from development/values/data_value_action.i

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.jumps.*;
import ideal.development.notifications.*;
import ideal.development.flags.*;

import javax.annotation.Nullable;

public class data_value_action<value_type extends readonly_data_value> extends base_value_action<value_type> {
  public data_value_action(final value_type the_value, final origin the_origin) {
    super(the_value, the_origin);
  }
  private base_value_action<data_value> get_value_action() {
    return (base_value_action<data_value>) (base_value_action) this;
  }
  public @Override abstract_value result() {
    return this.get_value_action().the_value;
  }
  public @Override @Nullable declaration get_declaration() {
    return this.get_value_action().the_value.get_declaration();
  }
}
// Autogenerated from development/values/list_value.i

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

public class list_value extends debuggable implements composite_wrapper {
  public final any_list<value_wrapper> the_list;
  private final type bound;
  public list_value(final any_list<value_wrapper> the_list, final type bound) {
    this.the_list = the_list;
    this.bound = bound;
  }
  public @Override type type_bound() {
    return this.bound;
  }
  public @Override any_list<value_wrapper> unwrap() {
    return this.the_list;
  }
  public @Override value_wrapper get_var(final variable_id key) {
    if (key.short_name() == common_names.size_name) {
      return new integer_value(((readonly_list<value_wrapper>) (Object) this.the_list).size(), common_types.immutable_nonnegative_type());
    }
    {
      utilities.panic(ideal.machine.elements.runtime_util.concatenate(new base_string("Failing list_value.get_var() for "), key));
      return null;
    }
  }
  public @Override void put_var(final variable_id key, final value_wrapper value) {
    utilities.panic(ideal.machine.elements.runtime_util.concatenate(new base_string("Failing list_value.put_var() for "), key));
  }
}

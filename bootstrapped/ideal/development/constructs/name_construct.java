// Autogenerated from development/constructs/name_construct.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class name_construct extends base_construct implements stringable {
  public final action_name the_name;
  public @Override string to_string() {
    return utilities.describe(this, this.the_name);
  }
  public name_construct(final action_name the_name, final origin generated_origin) {
    super(generated_origin);
    assert the_name != null;
    this.the_name = the_name;
  }
  public @Override readonly_list<construct> children() {
    final base_list<construct> generated_result = new base_list<construct>();
    return generated_result;
  }
}

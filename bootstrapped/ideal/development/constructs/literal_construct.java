// Autogenerated from development/constructs/literal_construct.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class literal_construct extends base_construct {
  public final literal the_literal;
  public literal_construct(final literal the_literal, final origin generated_origin) {
    super(generated_origin);
    assert the_literal != null;
    this.the_literal = the_literal;
  }
  public @Override readonly_list<construct> children() {
    final base_list<construct> generated_result = new base_list<construct>();
    return generated_result;
  }
}

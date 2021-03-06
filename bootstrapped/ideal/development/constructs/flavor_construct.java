// Autogenerated from development/constructs/flavor_construct.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class flavor_construct extends base_construct {
  public final type_flavor flavor;
  public final construct expr;
  public flavor_construct(final type_flavor flavor, final construct expr, final origin generated_origin) {
    super(generated_origin);
    assert flavor != null;
    this.flavor = flavor;
    assert expr != null;
    this.expr = expr;
  }
  public @Override readonly_list<construct> children() {
    final base_list<construct> generated_result = new base_list<construct>();
    generated_result.append(this.expr);
    return generated_result;
  }
}

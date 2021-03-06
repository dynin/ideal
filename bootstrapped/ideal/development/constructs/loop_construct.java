// Autogenerated from development/constructs/loop_construct.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class loop_construct extends base_construct {
  public final construct body;
  public loop_construct(final construct body, final origin generated_origin) {
    super(generated_origin);
    assert body != null;
    this.body = body;
  }
  public @Override readonly_list<construct> children() {
    final base_list<construct> generated_result = new base_list<construct>();
    generated_result.append(this.body);
    return generated_result;
  }
}

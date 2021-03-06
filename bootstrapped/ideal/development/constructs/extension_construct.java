// Autogenerated from development/constructs/extension_construct.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.library.texts.*;
import ideal.development.components.*;

public abstract class extension_construct extends base_construct {
  public abstract readonly_list<construct> children();
  public abstract analyzable to_analyzable();
  public abstract text_fragment print(printer p);
  public abstract boolean is_terminated();
  public abstract construct transform(transformer t);
  public extension_construct(final origin generated_origin) {
    super(generated_origin);
  }
}

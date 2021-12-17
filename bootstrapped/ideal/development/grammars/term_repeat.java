// Autogenerated from development/grammars/term_repeat.i

package ideal.development.grammars;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.constructs.*;
import ideal.development.scanners.*;

public enum term_repeat implements deeply_immutable_data, stringable {
  NO_REPEAT,
  ZERO_OR_MORE,
  ONE_OR_MORE;
  public string to_string() {
    return new base_string(toString());
  }
}
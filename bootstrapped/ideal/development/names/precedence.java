// Autogenerated from development/names/precedence.i

package ideal.development.names;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.machine.characters.unicode_handler;

public enum precedence implements displayable, enum_data {
  POSTFIX,
  UNARY,
  MULTIPLICATIVE,
  ADDITIVE,
  CONCATENATE,
  SHIFT,
  RELATIONAL,
  EQUALITY,
  BITWISE_AND,
  BITWISE_XOR,
  BITWISE_OR,
  LOGICAL_AND,
  LOGICAL_OR,
  TERNARY,
  ASSIGNMENT;
  public @Override string display() {
    return this.to_string();
  }
  public string to_string() {
    return new base_string(toString());
  }
}

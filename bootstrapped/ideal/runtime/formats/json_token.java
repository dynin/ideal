// Autogenerated from runtime/formats/json_token.i

package ideal.runtime.formats;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.formats.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;

public enum json_token implements enum_data {
  OPEN_BRACE('{'),
  CLOSE_BRACE('}'),
  OPEN_BRACKET('['),
  CLOSE_BRACKET(']'),
  COMMA(','),
  COLON(':');
  public final char the_character;
  private json_token(final char the_character) {
    this.the_character = the_character;
  }
  public string to_string() {
    return new base_string(toString());
  }
}

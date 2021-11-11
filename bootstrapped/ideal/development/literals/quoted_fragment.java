// Autogenerated from development/literals/quoted_fragment.i

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.machine.channels.string_writer;

public class quoted_fragment extends literal_fragment {
  public final quoted_character the_quoted_character;
  public quoted_fragment(final quoted_character the_quoted_character) {
    this.the_quoted_character = the_quoted_character;
  }
  public @Override string to_string() {
    return this.the_quoted_character.with_escape();
  }
}

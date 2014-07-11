// Autogenerated from isource/runtime/texts/text_entity.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

public class text_entity extends debuggable implements special_text, reference_equality {
  private final text_namespace the_namespace;
  private final string plain_text;
  private final string markup;
  public text_entity(final text_namespace the_namespace, final string plain_text, final string markup) {
    this.the_namespace = the_namespace;
    this.plain_text = plain_text;
    this.markup = markup;
  }
  public @Override string to_plain_text() {
    return plain_text;
  }
  public @Override string to_markup() {
    return markup;
  }
  public @Override string to_string() {
    return markup;
  }
}

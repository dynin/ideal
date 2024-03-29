// Autogenerated from development/literals/string_literal.i

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.machine.channels.string_writer;

public class string_literal extends debuggable implements literal<string> {
  public final string string_value;
  public final immutable_list<literal_fragment> content;
  public final quote_type quote;
  public string_literal(final string string_value, final immutable_list<literal_fragment> content, final quote_type quote) {
    this.string_value = string_value;
    this.content = content;
    this.quote = quote;
  }
  public string_literal(final string string_value, final quote_type quote) {
    this.string_value = string_value;
    this.content = string_literal.escape_content(string_value);
    this.quote = quote;
  }
  public @Override string the_value() {
    return this.string_value;
  }
  public string content_with_escapes() {
    final string_writer the_string_writer = new string_writer();
    {
      final readonly_list<literal_fragment> fragment_list = this.content;
      for (Integer fragment_index = 0; fragment_index < fragment_list.size(); fragment_index += 1) {
        final literal_fragment fragment = fragment_list.get(fragment_index);
        the_string_writer.write_all(fragment.to_string());
      }
    }
    return the_string_writer.elements();
  }
  public @Override string to_string() {
    return this.content_with_escapes();
  }
  private static immutable_list<literal_fragment> escape_content(final string input) {
    final base_list<literal_fragment> result = new base_list<literal_fragment>();
    Integer start_index = 0;
    {
      final readonly_list<Integer> index_list = input.indexes();
      for (Integer index_index = 0; index_index < index_list.size(); index_index += 1) {
        final Integer index = index_list.get(index_index);
        final char the_character = input.get(index);
        {
          final readonly_list<quoted_character> quoted_list = quoted_character.java_list;
          for (Integer quoted_index = 0; quoted_index < quoted_list.size(); quoted_index += 1) {
            final quoted_character quoted = quoted_list.get(quoted_index);
            if (the_character == quoted.value_character) {
              if (start_index < index) {
                result.append(new string_fragment(input.slice(start_index, index)));
              }
              result.append(new quoted_fragment(quoted));
              start_index = index + 1;
              break;
            }
          }
        }
      }
    }
    if (start_index < input.size()) {
      result.append(new string_fragment(input.skip(start_index)));
    }
    return result.frozen_copy();
  }
}

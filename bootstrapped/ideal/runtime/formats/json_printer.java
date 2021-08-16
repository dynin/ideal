// Autogenerated from runtime/formats/json_printer.i

package ideal.runtime.formats;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.machine.channels.string_writer;

public class json_printer {
  public final character_handler the_character_handler;
  public json_printer(final character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
  }
  public string print(final Object the_value) {
    final string_writer result = new string_writer();
    this.print_value(the_value, result);
    return result.elements();
  }
  private void print_value(final Object the_value, final string_writer result) {
    if (the_value instanceof string) {
      this.print_string(((string) the_value), result);
    } else if (the_value instanceof Integer) {
      this.print_integer(((Integer) the_value), result);
    } else if (the_value instanceof readonly_list) {
      this.print_list(((readonly_list<Object>) the_value), result);
    } else if (the_value instanceof dictionary) {
      this.print_dictionary(((dictionary<string, Object>) the_value), result);
    } else if (the_value == null) {
      result.write_all(new base_string("null"));
    } else {
      utilities.panic(new base_string("Unknown JSON value"));
    }
  }
  private void print_string(final string the_string, final string_writer result) {
    result.write('\"');
    {
      final readonly_list<Character> the_character_list = (immutable_list<Character>) the_string;
      for (Integer the_character_index = 0; the_character_index < the_character_list.size(); the_character_index += 1) {
        final char the_character = the_character_list.get(the_character_index);
        if (the_character == '\"' || the_character == '\\' || the_character == '/') {
          result.write('\\');
          result.write(the_character);
        } else if (the_character == '\b') {
          result.write_all(new base_string("\\b"));
        } else if (the_character == '\f') {
          result.write_all(new base_string("\\f"));
        } else if (the_character == '\n') {
          result.write_all(new base_string("\\n"));
        } else if (the_character == '\r') {
          result.write_all(new base_string("\\r"));
        } else if (the_character == '\t') {
          result.write_all(new base_string("\\t"));
        } else {
          result.write(the_character);
        }
      }
    }
    result.write('\"');
  }
  private void print_integer(final Integer the_integer, final string_writer result) {
    result.write_all(ideal.machine.elements.runtime_util.int_to_string(the_integer));
  }
  private void print_list(final readonly_list<Object> the_list, final string_writer result) {
    result.write(json_token.OPEN_BRACKET.the_character);
    boolean start = true;
    {
      final readonly_list<Object> element_list = the_list;
      for (Integer element_index = 0; element_index < element_list.size(); element_index += 1) {
        final Object element = element_list.get(element_index);
        if (start) {
          start = false;
        } else {
          result.write(json_token.COMMA.the_character);
          result.write(' ');
        }
        this.print_value(element, result);
      }
    }
    result.write(json_token.CLOSE_BRACKET.the_character);
  }
  private void print_dictionary(final dictionary<string, Object> the_dictionary, final string_writer result) {
    result.write(json_token.OPEN_BRACE.the_character);
    boolean start = true;
    {
      final readonly_list<dictionary.entry<string, Object>> element_list = the_dictionary.elements();
      for (Integer element_index = 0; element_index < element_list.size(); element_index += 1) {
        final dictionary.entry<string, Object> element = element_list.get(element_index);
        if (start) {
          start = false;
        } else {
          result.write(json_token.COMMA.the_character);
          result.write(' ');
        }
        this.print_string(element.key(), result);
        result.write(json_token.COLON.the_character);
        result.write(' ');
        this.print_value(element.value(), result);
      }
    }
    result.write(json_token.CLOSE_BRACE.the_character);
  }
}

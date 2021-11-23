// Autogenerated from development/scanners/integer_token_element.i

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.runtime.patterns.*;
import ideal.runtime.logs.*;
import ideal.machine.characters.*;
import ideal.machine.channels.string_writer;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.notifications.*;
import ideal.development.origins.*;
import ideal.development.comments.*;
import ideal.development.literals.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.constraint_category;
import ideal.development.jumps.jump_category;

import javax.annotation.Nullable;

public class integer_token_element extends base_scanner_element {
  public @Override @Nullable scan_state process(final source_content source, final Integer begin) {
    final string input = source.content;
    assert begin < input.size();
    final char first = input.get(begin);
    if (!this.the_character_handler().is_digit(first)) {
      return null;
    }
    Integer end = begin + 1;
    Integer radix;
    Integer value;
    if (end < input.size() && this.the_character_handler().to_lower_case(input.get(end)) == 'x') {
      radix = 16;
      value = 0;
      end += 1;
    } else {
      radix = radixes.DEFAULT_RADIX;
      final @Nullable Integer digit = this.the_character_handler().from_digit(first, radix);
      assert digit >= 0;
      value = digit;
    }
    while (end < input.size()) {
      final @Nullable Integer digit = this.the_character_handler().from_digit(input.get(end), radix);
      if (digit == null) {
        break;
      }
      value = value * radix + digit;
      end += 1;
    }
    final string image = input.slice(begin, end);
    final origin the_origin = source.make_origin(begin, end);
    final integer_literal int_literal = new integer_literal(value, image, radix);
    return new scan_state(((token<Object>) (Object) new base_token<integer_literal>(special_token_type.LITERAL, int_literal, the_origin)), end, end);
  }
  public integer_token_element() { }
}
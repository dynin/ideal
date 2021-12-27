// Autogenerated from development/scanners/scanner_config.i

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

public interface readonly_scanner_config extends readonly_value, any_scanner_config {
  character_handler the_character_handler();
  boolean is_whitespace(char the_character);
  boolean is_name_start(char the_character);
  boolean is_name_part(char the_character);
  readonly_list<scanner_element> elements();
  token<Object> process_token(token<Object> the_token);
}

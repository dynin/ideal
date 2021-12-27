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

import javax.annotation.Nullable;

public interface scanner_config extends value, readonly_scanner_config, writeonly_scanner_config {
  void add_keyword(keyword the_keyword);
  void add_punctuation(punctuation_type the_punctuation_type);
  void add_special(special_name the_special_name, token_type the_token_type);
  void add_kind(kind the_kind);
  void add_subtype_tag(subtype_tag the_subtype_tag);
  void add_modifier(modifier_kind the_modifier_kind);
  void add_flavor(type_flavor the_type_flavor);
  void add_reserved(string reserved_word, @Nullable keyword the_keyword);
  readonly_list<token<Object>> scan(source_content source);
}

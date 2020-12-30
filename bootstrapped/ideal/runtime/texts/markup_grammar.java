// Autogenerated from runtime/texts/markup_grammar.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;
import ideal.library.patterns.*;
import ideal.runtime.patterns.*;
import ideal.runtime.texts.character_patterns.*;

public class markup_grammar {
  public final character_handler the_character_handler;
  public final pattern<Character> document_pattern;
  public markup_grammar(final character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    this.document_pattern = this.document();
  }
  protected boolean name_start(final char c) {
    return this.the_character_handler.is_letter(c) || c == '_' || c == ':';
  }
  protected boolean name_char(final char c) {
    return this.the_character_handler.is_letter(c) || c == '.' || c == '-' || c == '_' || c == ':';
  }
  protected boolean content_char(final char c) {
    return c != '<' && c != '&';
  }
  protected pattern<Character> document() {
    final pattern<Character> space_opt = character_patterns.zero_or_more(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.the_character_handler.is_whitespace(first);
      }
    });
    final matcher<Character, string> name = character_patterns.as_string(new sequence_pattern<Character>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ character_patterns.one(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.name_start(first);
      }
    }), character_patterns.zero_or_more(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.name_char(first);
      }
    }) }))));
    final pattern<Character> lt = character_patterns.one_character('<');
    final pattern<Character> gt = character_patterns.one_character('>');
    final pattern<Character> slash = character_patterns.one_character('/');
    final pattern<Character> empty_element = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, name, space_opt, slash, gt })));
    final option_pattern<Character> element = character_patterns.option(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ empty_element })));
    final pattern<Character> char_data_opt = character_patterns.zero_or_more(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.content_char(first);
      }
    });
    final pattern<Character> content = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ char_data_opt, character_patterns.repeat_or_none(character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ element, char_data_opt })))) })));
    final pattern<Character> start_tag = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, name, space_opt, gt })));
    final pattern<Character> end_tag = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, slash, name, space_opt, gt })));
    element.add_option(character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ start_tag, content, end_tag }))));
    return character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, element, space_opt })));
  }
}

// Autogenerated from runtime/texts/character_patterns.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;
import ideal.library.patterns.*;
import ideal.runtime.patterns.*;

public class character_patterns {
  public static pattern<Character> one(final function1<Boolean, Character> the_predicate) {
    return new predicate_pattern<Character>(the_predicate);
  }
  public static pattern<Character> one_character(final char the_character) {
    return new singleton_pattern<Character>(the_character);
  }
  public static pattern<Character> zero_or_more(final function1<Boolean, Character> the_predicate) {
    return new repeat_element<Character>(the_predicate, true);
  }
  public static pattern<Character> one_or_more(final function1<Boolean, Character> the_predicate) {
    return new repeat_element<Character>(the_predicate, false);
  }
  public static pattern<Character> repeat_or_none(final pattern<Character> the_pattern) {
    return new repeat_pattern<Character>(the_pattern, true);
  }
  public static pattern<Character> sequence(final readonly_list<pattern<Character>> patterns_list) {
    return new sequence_pattern<Character>(patterns_list);
  }
  public static option_pattern<Character> option(final readonly_list<pattern<Character>> patterns_list) {
    return new option_pattern<Character>(patterns_list);
  }
  public static option_matcher<Character, attribute_fragment> option_fragment_list(final readonly_list<matcher<Character, attribute_fragment>> matchers) {
    return new option_matcher<Character, attribute_fragment>(matchers);
  }
  public static option_matcher<Character, text_fragment> option_text_fragment(final readonly_list<matcher<Character, text_fragment>> matchers) {
    return new option_matcher<Character, text_fragment>(matchers);
  }
  public static option_matcher<Character, attribute_fragment> option_fragment(final matcher<Character, string> attr_value, final matcher<Character, special_text> entity_ref) {
    return character_patterns.option_fragment_list(new base_immutable_list<matcher<Character, attribute_fragment>>(new ideal.machine.elements.array<matcher<Character, attribute_fragment>>(new matcher[]{ (matcher<Character, attribute_fragment>) (matcher) entity_ref, (matcher<Character, attribute_fragment>) (matcher) attr_value })));
  }
  public static attribute_fragment join_fragments(final readonly_list<attribute_fragment> fragments) {
    return text_util.join_attributes(fragments);
  }
  public static matcher<Character, attribute_fragment> repeat_or_none_fragment(final matcher<Character, attribute_fragment> the_matcher) {
    return new repeat_matcher<Character, attribute_fragment, attribute_fragment>(the_matcher, true, new procedure1<attribute_fragment, readonly_list<attribute_fragment>>() {
      @Override public attribute_fragment call(readonly_list<attribute_fragment> first) {
        return character_patterns.join_fragments(first);
      }
    });
  }
  public static text_fragment join_fragments_text(final readonly_list<text_fragment> fragments) {
    return text_util.join(fragments);
  }
  public static matcher<Character, text_fragment> repeat_or_none_text(final matcher<Character, text_fragment> the_matcher) {
    return new repeat_matcher<Character, text_fragment, text_fragment>(the_matcher, true, new procedure1<text_fragment, readonly_list<text_fragment>>() {
      @Override public text_fragment call(readonly_list<text_fragment> first) {
        return character_patterns.join_fragments_text(first);
      }
    });
  }
  public static immutable_list<attribute_state> cast_attributes(final readonly_list<attribute_state> attributes) {
    return (immutable_list<attribute_state>) (immutable_list) attributes.elements();
  }
  public static matcher<Character, immutable_list<attribute_state>> repeat_or_none_attribute(final matcher<Character, attribute_state> the_matcher) {
    return new repeat_matcher<Character, immutable_list<attribute_state>, attribute_state>(the_matcher, true, new procedure1<immutable_list<attribute_state>, readonly_list<attribute_state>>() {
      @Override public immutable_list<attribute_state> call(readonly_list<attribute_state> first) {
        return character_patterns.cast_attributes(first);
      }
    });
  }
  public static string as_string_procedure(final readonly_list<Character> the_character_list) {
    return (base_string) the_character_list.frozen_copy();
  }
  public static matcher<Character, string> as_string(final pattern<Character> the_pattern) {
    return new procedure_matcher<Character, string>(the_pattern, new function1<string, readonly_list<Character>>() {
      @Override public string call(readonly_list<Character> first) {
        return character_patterns.as_string_procedure(first);
      }
    });
  }
  public static attribute_fragment select_2nd_attribute_fragment(final readonly_list<any_value> the_list) {
    return (attribute_fragment) the_list.get(1);
  }
}

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

import javax.annotation.Nullable;

public class markup_grammar {
  public final character_handler the_character_handler;
  public final dictionary<string, element_id> element_ids;
  public final dictionary<string, attribute_id> attribute_ids;
  public final dictionary<string, special_text> entities;
  public pattern<Character> document_pattern;
  public matcher<Character, special_text> entity_ref;
  public matcher<Character, string> quot_attr_value;
  public matcher<Character, string> apos_attr_value;
  public matcher<Character, attribute_fragment> attribute_value_in_quot;
  public matcher<Character, attribute_fragment> attribute_value_in_apos;
  public matcher<Character, attribute_state> attribute;
  public matcher<Character, text_element> empty_element;
  public matcher<Character, text_fragment> content;
  public markup_grammar(final character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    this.element_ids = new hash_dictionary<string, element_id>();
    this.attribute_ids = new hash_dictionary<string, attribute_id>();
    this.entities = new hash_dictionary<string, special_text>();
  }
  private boolean is_completed() {
    return this.document_pattern != null;
  }
  public void add_elements(final readonly_collection<element_id> new_element_ids) {
    assert !this.is_completed();
    {
      final readonly_list<element_id> the_element_id_list = new_element_ids.elements();
      for (int the_element_id_index = 0; the_element_id_index < the_element_id_list.size(); the_element_id_index += 1) {
        final element_id the_element_id = the_element_id_list.get(the_element_id_index);
        assert !this.element_ids.contains_key(the_element_id.short_name());
        this.element_ids.put(the_element_id.short_name(), the_element_id);
      }
    }
  }
  public void add_attributes(final readonly_collection<attribute_id> new_attribute_ids) {
    assert !this.is_completed();
    {
      final readonly_list<attribute_id> the_attribute_id_list = new_attribute_ids.elements();
      for (int the_attribute_id_index = 0; the_attribute_id_index < the_attribute_id_list.size(); the_attribute_id_index += 1) {
        final attribute_id the_attribute_id = the_attribute_id_list.get(the_attribute_id_index);
        assert !this.attribute_ids.contains_key(the_attribute_id.short_name());
        this.attribute_ids.put(the_attribute_id.short_name(), the_attribute_id);
      }
    }
  }
  public void add_entities(final readonly_collection<special_text> new_entities) {
    assert !this.is_completed();
    {
      final readonly_list<special_text> the_entity_list = new_entities.elements();
      for (int the_entity_index = 0; the_entity_index < the_entity_list.size(); the_entity_index += 1) {
        final special_text the_entity = the_entity_list.get(the_entity_index);
        assert !this.entities.contains_key(the_entity.name());
        this.entities.put(the_entity.name(), the_entity);
      }
    }
  }
  public void complete() {
    assert !this.is_completed();
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
  protected boolean content_not_apos(final char c) {
    return c != '<' && c != '&' && c != '\'';
  }
  protected boolean content_not_quot(final char c) {
    return c != '<' && c != '&' && c != '\"';
  }
  public text_element match_empty_element(final readonly_list<any_value> the_list) {
    final string element_name = (string) the_list.get(1);
    final @Nullable element_id element_id = this.element_ids.get(element_name);
    assert element_id != null;
    final immutable_list<attribute_state> attributes = (immutable_list<attribute_state>) the_list.get(2);
    final dictionary<attribute_id, attribute_fragment> attributes_dictionary = new list_dictionary<attribute_id, attribute_fragment>();
    {
      final readonly_list<attribute_state> attribute_list = attributes;
      for (int attribute_index = 0; attribute_index < attribute_list.size(); attribute_index += 1) {
        final attribute_state attribute = attribute_list.get(attribute_index);
        attributes_dictionary.put(attribute.id, attribute.value);
      }
    }
    return new base_element(element_id, attributes_dictionary, null);
  }
  public special_text make_entity_2nd(final readonly_list<any_value> the_list) {
    final string entity_name = (string) the_list.get(1);
    final @Nullable special_text entity = this.entities.get(entity_name);
    assert entity != null;
    return entity;
  }
  public attribute_state make_attribute(final readonly_list<any_value> the_list) {
    final string attribute_name = (string) the_list.get(0);
    final @Nullable attribute_id id = this.attribute_ids.get(attribute_name);
    assert id != null;
    final attribute_fragment value = (attribute_fragment) the_list.get(2);
    return new attribute_state(id, value);
  }
  public attribute_state select_2nd_attribute_state(final readonly_list<any_value> the_list) {
    return (attribute_state) the_list.get(1);
  }
  public text_fragment join2(final readonly_list<any_value> the_list) {
    assert the_list.size() == 2;
    return text_util.join((text_fragment) the_list.get(0), (text_fragment) the_list.get(1));
  }
  protected pattern<Character> document() {
    final pattern<Character> lt = character_patterns.one_character('<');
    final pattern<Character> gt = character_patterns.one_character('>');
    final pattern<Character> slash = character_patterns.one_character('/');
    final pattern<Character> amp = character_patterns.one_character('&');
    final pattern<Character> semicolon = character_patterns.one_character(';');
    final pattern<Character> quot = character_patterns.one_character('\"');
    final pattern<Character> apos = character_patterns.one_character('\'');
    final pattern<Character> eq = character_patterns.one_character('=');
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
    this.entity_ref = new sequence_matcher<Character, special_text>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ amp, name, semicolon })), new function1<special_text, readonly_list<any_value>>() {
      @Override public special_text call(readonly_list<any_value> first) {
        return markup_grammar.this.make_entity_2nd(first);
      }
    });
    final pattern<Character> equals = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, eq, space_opt })));
    this.quot_attr_value = character_patterns.as_string(character_patterns.one_or_more(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.content_not_quot(first);
      }
    }));
    this.attribute_value_in_quot = new sequence_matcher<Character, attribute_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ quot, character_patterns.repeat_or_none_fragment(character_patterns.option_fragment(this.quot_attr_value, this.entity_ref)), quot })), new function1<attribute_fragment, readonly_list<any_value>>() {
      @Override public attribute_fragment call(readonly_list<any_value> first) {
        return character_patterns.select_2nd_attribute_fragment(first);
      }
    });
    this.apos_attr_value = character_patterns.as_string(character_patterns.one_or_more(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.content_not_apos(first);
      }
    }));
    this.attribute_value_in_apos = new sequence_matcher<Character, attribute_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ apos, character_patterns.repeat_or_none_fragment(character_patterns.option_fragment(this.apos_attr_value, this.entity_ref)), apos })), new function1<attribute_fragment, readonly_list<any_value>>() {
      @Override public attribute_fragment call(readonly_list<any_value> first) {
        return character_patterns.select_2nd_attribute_fragment(first);
      }
    });
    final option_matcher<Character, attribute_fragment> attribute_value = character_patterns.option_fragment_list(new base_immutable_list<matcher<Character, attribute_fragment>>(new ideal.machine.elements.array<matcher<Character, attribute_fragment>>(new matcher[]{ this.attribute_value_in_quot, this.attribute_value_in_apos })));
    this.attribute = new sequence_matcher<Character, attribute_state>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ name, equals, attribute_value })), new function1<attribute_state, readonly_list<any_value>>() {
      @Override public attribute_state call(readonly_list<any_value> first) {
        return markup_grammar.this.make_attribute(first);
      }
    });
    final matcher<Character, immutable_list<attribute_state>> attributes = character_patterns.repeat_or_none_attribute(new sequence_matcher<Character, attribute_state>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, this.attribute })), new function1<attribute_state, readonly_list<any_value>>() {
      @Override public attribute_state call(readonly_list<any_value> first) {
        return markup_grammar.this.select_2nd_attribute_state(first);
      }
    }));
    this.empty_element = new sequence_matcher<Character, text_element>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, name, attributes, space_opt, slash, gt })), new function1<text_element, readonly_list<any_value>>() {
      @Override public text_element call(readonly_list<any_value> first) {
        return markup_grammar.this.match_empty_element(first);
      }
    });
    final option_matcher<Character, text_fragment> element = character_patterns.option_text_fragment(new base_immutable_list<matcher<Character, text_fragment>>(new ideal.machine.elements.array<matcher<Character, text_fragment>>(new matcher[]{ (matcher<Character, text_fragment>) (matcher) this.empty_element })));
    final matcher<Character, string> char_data_opt = character_patterns.as_string(character_patterns.zero_or_more(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return markup_grammar.this.content_char(first);
      }
    }));
    final option_matcher<Character, text_fragment> content_element = character_patterns.option_text_fragment(new base_immutable_list<matcher<Character, text_fragment>>(new ideal.machine.elements.array<matcher<Character, text_fragment>>(new matcher[]{ element, (matcher<Character, text_fragment>) (matcher) this.entity_ref })));
    final matcher<Character, text_fragment> content_tail = character_patterns.repeat_or_none_text(new sequence_matcher<Character, text_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ (pattern<Character>) content_element, char_data_opt })), new function1<text_fragment, readonly_list<any_value>>() {
      @Override public text_fragment call(readonly_list<any_value> first) {
        return markup_grammar.this.join2(first);
      }
    }));
    this.content = new sequence_matcher<Character, text_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ char_data_opt, (pattern<Character>) content_tail })), new function1<text_fragment, readonly_list<any_value>>() {
      @Override public text_fragment call(readonly_list<any_value> first) {
        return markup_grammar.this.join2(first);
      }
    });
    final pattern<Character> start_tag = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, name, attributes, space_opt, gt })));
    final pattern<Character> end_tag = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, slash, name, space_opt, gt })));
    element.add_option(character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ start_tag, this.content, end_tag }))));
    final pattern<Character> result = character_patterns.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, element, space_opt })));
    ((validatable) result).validate();
    return result;
  }
}

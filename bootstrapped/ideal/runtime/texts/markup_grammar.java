// Autogenerated from runtime/texts/markup_grammar.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;
import ideal.library.patterns.*;
import ideal.runtime.patterns.*;

import javax.annotation.Nullable;

public class markup_grammar implements value {
  public final character_handler the_character_handler;
  public final dictionary<string, element_id> element_ids;
  public final dictionary<string, attribute_id> attribute_ids;
  public final dictionary<string, special_text> entities;
  public matcher<Character, text_element> document_matcher;
  public matcher<Character, special_text> entity_ref;
  public matcher<Character, string> quot_attr_value;
  public matcher<Character, string> apos_attr_value;
  public matcher<Character, attribute_fragment> attribute_value_in_quot;
  public matcher<Character, attribute_fragment> attribute_value_in_apos;
  public matcher<Character, attribute_state> attribute;
  public option_matcher<Character, text_element> element;
  public matcher<Character, text_element> empty_element;
  public matcher<Character, text_fragment> content;
  public markup_parser parser;
  public markup_grammar(final character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    this.element_ids = new hash_dictionary<string, element_id>();
    this.attribute_ids = new hash_dictionary<string, attribute_id>();
    this.entities = new hash_dictionary<string, special_text>();
  }
  private boolean is_completed() {
    return this.document_matcher != null;
  }
  public void add_elements(final readonly_collection<element_id> new_element_ids) {
    assert !this.is_completed();
    {
      final readonly_list<element_id> the_element_id_list = new_element_ids.elements();
      for (Integer the_element_id_index = 0; the_element_id_index < the_element_id_list.size(); the_element_id_index += 1) {
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
      for (Integer the_attribute_id_index = 0; the_attribute_id_index < the_attribute_id_list.size(); the_attribute_id_index += 1) {
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
      for (Integer the_entity_index = 0; the_entity_index < the_entity_list.size(); the_entity_index += 1) {
        final special_text the_entity = the_entity_list.get(the_entity_index);
        assert !this.entities.contains_key(the_entity.name());
        this.entities.put(the_entity.name(), the_entity);
      }
    }
  }
  public void complete() {
    assert !this.is_completed();
    this.document_matcher = this.document();
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
  public pattern<Character> one(final function1<Boolean, Character> the_predicate) {
    return new predicate_pattern<Character>(the_predicate);
  }
  public pattern<Character> one_character(final char the_character) {
    return new singleton_pattern<Character>(the_character);
  }
  public pattern<Character> zero_or_more(final function1<Boolean, Character> the_predicate) {
    return new repeat_element<Character>(the_predicate, true);
  }
  public pattern<Character> one_or_more(final function1<Boolean, Character> the_predicate) {
    return new repeat_element<Character>(the_predicate, false);
  }
  public pattern<Character> repeat_or_none(final pattern<Character> the_pattern) {
    return new repeat_pattern<Character>(the_pattern, true);
  }
  public pattern<Character> sequence(final readonly_list<pattern<Character>> patterns_list) {
    return new sequence_pattern<Character>(patterns_list);
  }
  public option_pattern<Character> option(final readonly_list<pattern<Character>> patterns_list) {
    return new option_pattern<Character>(patterns_list);
  }
  public option_matcher<Character, attribute_fragment> option_fragment_list(final readonly_list<matcher<Character, attribute_fragment>> matchers) {
    return new option_matcher<Character, attribute_fragment>(matchers);
  }
  public option_matcher<Character, text_fragment> option_text_fragment(final readonly_list<matcher<Character, text_fragment>> matchers) {
    return new option_matcher<Character, text_fragment>(matchers);
  }
  public option_matcher<Character, text_element> option_text_element(final readonly_list<matcher<Character, text_element>> matchers) {
    return new option_matcher<Character, text_element>(matchers);
  }
  public option_matcher<Character, attribute_fragment> option_fragment(final matcher<Character, string> attr_value, final matcher<Character, special_text> entity_ref) {
    return this.option_fragment_list(new base_immutable_list<matcher<Character, attribute_fragment>>(new ideal.machine.elements.array<matcher<Character, attribute_fragment>>(new matcher[]{ (matcher<Character, attribute_fragment>) (Object) entity_ref, (matcher<Character, attribute_fragment>) (Object) attr_value })));
  }
  public attribute_fragment join_fragments(final readonly_list<attribute_fragment> fragments) {
    return text_utilities.join_attributes(fragments);
  }
  public matcher<Character, attribute_fragment> repeat_or_none_fragment(final matcher<Character, attribute_fragment> the_matcher) {
    return ((matcher<Character, attribute_fragment>) (Object) new repeat_matcher<Character, attribute_fragment, attribute_fragment>(the_matcher, true, new function1<attribute_fragment, readonly_list<attribute_fragment>>() {
      public @Override attribute_fragment call(readonly_list<attribute_fragment> first) {
        return markup_grammar.this.join_fragments(first);
      }
    }));
  }
  public text_fragment join_fragments_text(final readonly_list<text_fragment> fragments) {
    return text_utilities.join(fragments);
  }
  public matcher<Character, text_fragment> repeat_or_none_text(final matcher<Character, text_fragment> the_matcher) {
    return ((matcher<Character, text_fragment>) (Object) new repeat_matcher<Character, text_fragment, text_fragment>(the_matcher, true, new function1<text_fragment, readonly_list<text_fragment>>() {
      public @Override text_fragment call(readonly_list<text_fragment> first) {
        return markup_grammar.this.join_fragments_text(first);
      }
    }));
  }
  public immutable_list<attribute_state> cast_attributes(final readonly_list<attribute_state> attributes) {
    return (immutable_list<attribute_state>) (Object) attributes.elements();
  }
  public matcher<Character, immutable_list<attribute_state>> repeat_or_none_attribute(final matcher<Character, attribute_state> the_matcher) {
    return ((matcher<Character, immutable_list<attribute_state>>) (Object) new repeat_matcher<Character, immutable_list<attribute_state>, attribute_state>(the_matcher, true, new function1<immutable_list<attribute_state>, readonly_list<attribute_state>>() {
      public @Override immutable_list<attribute_state> call(readonly_list<attribute_state> first) {
        return markup_grammar.this.cast_attributes(first);
      }
    }));
  }
  public string as_string_procedure(final readonly_list<Character> the_character_list) {
    return (string) the_character_list.frozen_copy();
  }
  public matcher<Character, string> as_string(final pattern<Character> the_pattern) {
    return new procedure_matcher<Character, string>(the_pattern, new function1<string, readonly_list<Character>>() {
      public @Override string call(readonly_list<Character> first) {
        return markup_grammar.this.as_string_procedure(first);
      }
    });
  }
  public text_element match_start_element(final readonly_list<Object> the_list) {
    final string element_name = (string) the_list.get(1);
    final immutable_list<attribute_state> attributes = (immutable_list<attribute_state>) the_list.get(2);
    final @Nullable element_id element_id = this.element_ids.get(element_name);
    if (element_id == null) {
      this.parser.report_error(ideal.machine.elements.runtime_util.concatenate(new base_string("Unrecognized element name: "), element_name));
      return new base_element(text_library.ERROR_ELEMENT);
    }
    final dictionary<attribute_id, attribute_fragment> attributes_dictionary = new list_dictionary<attribute_id, attribute_fragment>();
    {
      final readonly_list<attribute_state> attribute_list = attributes;
      for (Integer attribute_index = 0; attribute_index < attribute_list.size(); attribute_index += 1) {
        final attribute_state attribute = attribute_list.get(attribute_index);
        attributes_dictionary.put(attribute.id, attribute.value);
      }
    }
    return new base_element(element_id, attributes_dictionary, null);
  }
  public text_element match_text_element(final readonly_list<Object> the_list) {
    final text_element start_tag = (text_element) the_list.get(0);
    final text_fragment text_content = (text_fragment) the_list.get(1);
    final string end_tag_name = (string) the_list.get(2);
    final string start_tag_name = start_tag.get_id().short_name();
    if (start_tag.get_id() != text_library.ERROR_ELEMENT && !ideal.machine.elements.runtime_util.values_equal(start_tag_name, end_tag_name)) {
      this.parser.report_error(ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(new base_string("Mismatched element name: start "), start_tag_name), new base_string(", end ")), end_tag_name));
    }
    return new base_element(start_tag.get_id(), start_tag.attributes(), text_content);
  }
  public special_text make_entity_2nd(final readonly_list<Object> the_list) {
    final string entity_name = (string) the_list.get(1);
    final @Nullable special_text entity = this.entities.get(entity_name);
    if (entity == null) {
      this.parser.report_error(ideal.machine.elements.runtime_util.concatenate(new base_string("Unrecognized entity: "), entity_name));
      return text_library.ERROR_ENTITY;
    }
    return entity;
  }
  public attribute_state make_attribute(final readonly_list<Object> the_list) {
    final string attribute_name = (string) the_list.get(0);
    final attribute_fragment value = (attribute_fragment) the_list.get(2);
    final @Nullable attribute_id id = this.attribute_ids.get(attribute_name);
    if (id == null) {
      this.parser.report_error(ideal.machine.elements.runtime_util.concatenate(new base_string("Unrecognized attribute name: "), attribute_name));
      return new attribute_state(text_library.ERROR_ATTRIBUTE, value);
    }
    return new attribute_state(id, value);
  }
  public string select_end_tag(final readonly_list<Object> the_list) {
    return (string) the_list.get(2);
  }
  public attribute_fragment select_2nd_attribute_fragment(final readonly_list<Object> the_list) {
    return (attribute_fragment) the_list.get(1);
  }
  public attribute_state select_2nd_attribute_state(final readonly_list<Object> the_list) {
    return (attribute_state) the_list.get(1);
  }
  public text_element select_2nd_text_element(final readonly_list<Object> the_list) {
    return (text_element) the_list.get(1);
  }
  public text_fragment join2(final readonly_list<Object> the_list) {
    assert ideal.machine.elements.runtime_util.values_equal(the_list.size(), 2);
    return text_utilities.join((text_fragment) the_list.get(0), (text_fragment) the_list.get(1));
  }
  protected matcher<Character, text_element> document() {
    final pattern<Character> lt = this.one_character('<');
    final pattern<Character> gt = this.one_character('>');
    final pattern<Character> slash = this.one_character('/');
    final pattern<Character> amp = this.one_character('&');
    final pattern<Character> semicolon = this.one_character(';');
    final pattern<Character> quot = this.one_character('\"');
    final pattern<Character> apos = this.one_character('\'');
    final pattern<Character> eq = this.one_character('=');
    final pattern<Character> space_opt = this.zero_or_more(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return markup_grammar.this.the_character_handler.is_whitespace(first);
      }
    });
    final matcher<Character, string> name = this.as_string(new sequence_pattern<Character>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ this.one(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return markup_grammar.this.name_start(first);
      }
    }), this.zero_or_more(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return markup_grammar.this.name_char(first);
      }
    }) }))));
    this.entity_ref = new sequence_matcher<Character, special_text>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ amp, ((pattern<Character>) (Object) name), semicolon })), new function1<special_text, readonly_list<Object>>() {
      public @Override special_text call(readonly_list<Object> first) {
        return markup_grammar.this.make_entity_2nd(first);
      }
    });
    final pattern<Character> equals = this.sequence(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, eq, space_opt })));
    this.quot_attr_value = this.as_string(this.one_or_more(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return markup_grammar.this.content_not_quot(first);
      }
    }));
    this.attribute_value_in_quot = new sequence_matcher<Character, attribute_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ quot, ((pattern<Character>) (Object) this.repeat_or_none_fragment(this.option_fragment(this.quot_attr_value, this.entity_ref))), quot })), new function1<attribute_fragment, readonly_list<Object>>() {
      public @Override attribute_fragment call(readonly_list<Object> first) {
        return markup_grammar.this.select_2nd_attribute_fragment(first);
      }
    });
    this.apos_attr_value = this.as_string(this.one_or_more(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return markup_grammar.this.content_not_apos(first);
      }
    }));
    this.attribute_value_in_apos = new sequence_matcher<Character, attribute_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ apos, ((pattern<Character>) (Object) this.repeat_or_none_fragment(this.option_fragment(this.apos_attr_value, this.entity_ref))), apos })), new function1<attribute_fragment, readonly_list<Object>>() {
      public @Override attribute_fragment call(readonly_list<Object> first) {
        return markup_grammar.this.select_2nd_attribute_fragment(first);
      }
    });
    final option_matcher<Character, attribute_fragment> attribute_value = this.option_fragment_list(new base_immutable_list<matcher<Character, attribute_fragment>>(new ideal.machine.elements.array<matcher<Character, attribute_fragment>>(new matcher[]{ this.attribute_value_in_quot, ((sequence_matcher<Character, attribute_fragment>) this.attribute_value_in_apos) })));
    this.attribute = new sequence_matcher<Character, attribute_state>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ ((pattern<Character>) (Object) name), equals, ((pattern<Character>) (Object) attribute_value) })), new function1<attribute_state, readonly_list<Object>>() {
      public @Override attribute_state call(readonly_list<Object> first) {
        return markup_grammar.this.make_attribute(first);
      }
    });
    final matcher<Character, immutable_list<attribute_state>> attributes = this.repeat_or_none_attribute(new sequence_matcher<Character, attribute_state>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, ((pattern<Character>) (Object) ((sequence_matcher<Character, attribute_state>) this.attribute)) })), new function1<attribute_state, readonly_list<Object>>() {
      public @Override attribute_state call(readonly_list<Object> first) {
        return markup_grammar.this.select_2nd_attribute_state(first);
      }
    }));
    this.empty_element = new sequence_matcher<Character, text_element>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, ((pattern<Character>) (Object) name), ((pattern<Character>) (Object) attributes), space_opt, slash, gt })), new function1<text_element, readonly_list<Object>>() {
      public @Override text_element call(readonly_list<Object> first) {
        return markup_grammar.this.match_start_element(first);
      }
    });
    this.element = this.option_text_element(new base_immutable_list<matcher<Character, text_element>>(new ideal.machine.elements.array<matcher<Character, text_element>>(new matcher[]{ (matcher<Character, text_element>) (Object) ((sequence_matcher<Character, text_element>) this.empty_element) })));
    final matcher<Character, text_fragment> element_fragment = (matcher<Character, text_fragment>) (Object) this.element;
    final option_matcher<Character, text_fragment> content_element = this.option_text_fragment(new base_immutable_list<matcher<Character, text_fragment>>(new ideal.machine.elements.array<matcher<Character, text_fragment>>(new matcher[]{ element_fragment, (matcher<Character, text_fragment>) (Object) this.entity_ref })));
    final matcher<Character, string> char_data_opt = this.as_string(this.zero_or_more(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return markup_grammar.this.content_char(first);
      }
    }));
    final matcher<Character, text_fragment> content_tail = this.repeat_or_none_text(new sequence_matcher<Character, text_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ (pattern<Character>) (Object) ((pattern<Character>) (Object) content_element), ((pattern<Character>) (Object) char_data_opt) })), new function1<text_fragment, readonly_list<Object>>() {
      public @Override text_fragment call(readonly_list<Object> first) {
        return markup_grammar.this.join2(first);
      }
    }));
    this.content = new sequence_matcher<Character, text_fragment>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ ((pattern<Character>) (Object) char_data_opt), (pattern<Character>) (Object) ((pattern<Character>) (Object) content_tail) })), new function1<text_fragment, readonly_list<Object>>() {
      public @Override text_fragment call(readonly_list<Object> first) {
        return markup_grammar.this.join2(first);
      }
    });
    final pattern<Character> start_tag = ((pattern<Character>) (Object) new sequence_matcher<Character, text_element>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, ((pattern<Character>) (Object) name), ((pattern<Character>) (Object) attributes), space_opt, gt })), new function1<text_element, readonly_list<Object>>() {
      public @Override text_element call(readonly_list<Object> first) {
        return markup_grammar.this.match_start_element(first);
      }
    }));
    final sequence_matcher<Character, string> end_tag = new sequence_matcher<Character, string>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ lt, slash, ((pattern<Character>) (Object) name), space_opt, gt })), new function1<string, readonly_list<Object>>() {
      public @Override string call(readonly_list<Object> first) {
        return markup_grammar.this.select_end_tag(first);
      }
    });
    ((option_pattern<Character>) (Object) this.element).add_option(((pattern<Character>) (Object) new sequence_matcher<Character, text_element>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ start_tag, ((pattern<Character>) (Object) this.content), ((pattern<Character>) (Object) end_tag) })), new function1<text_element, readonly_list<Object>>() {
      public @Override text_element call(readonly_list<Object> first) {
        return markup_grammar.this.match_text_element(first);
      }
    })));
    final sequence_matcher<Character, text_element> result = new sequence_matcher<Character, text_element>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ space_opt, ((pattern<Character>) (Object) this.element), space_opt })), new function1<text_element, readonly_list<Object>>() {
      public @Override text_element call(readonly_list<Object> first) {
        return markup_grammar.this.select_2nd_text_element(first);
      }
    });
    this.update_matchers();
    ((sequence_pattern<Character>) (Object) result).validate();
    return result;
  }
  public void update_matchers() { }
  public text_element parse(final string text, final markup_parser parser) {
    this.parser = parser;
    return this.document_matcher.parse(text);
  }
}

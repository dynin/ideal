-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- The grammar for a subset of XML.
---
--- Used https://cs.lmu.edu/~ray/notes/xmlgrammar/ as a reference.
class markup_grammar {
  implicit import ideal.library.patterns;
  implicit import ideal.runtime.patterns;

  character_handler the_character_handler;
  dictionary[string, element_id] element_ids;
  dictionary[string, attribute_id] attribute_ids;
  dictionary[string, special_text] entities;
  var matcher[character, text_element] document_matcher;
  var matcher[character, special_text] entity_ref;
  var matcher[character, string] quot_attr_value;
  var matcher[character, string] apos_attr_value;
  var matcher[character, attribute_fragment] attribute_value_in_quot;
  var matcher[character, attribute_fragment] attribute_value_in_apos;
  var matcher[character, attribute_state] attribute;
  var matcher[character, text_element] empty_element;
  var matcher[character, text_fragment] content;
  var markup_parser parser;

  markup_grammar(character_handler the_character_handler) {
    this.the_character_handler = the_character_handler;
    element_ids = hash_dictionary[string, element_id].new();
    attribute_ids = hash_dictionary[string, attribute_id].new();
    entities = hash_dictionary[string, special_text].new();
  }

  private boolean is_completed => document_matcher is_not null;

  public void add_elements(readonly collection[element_id] new_element_ids) {
    assert !is_completed();
    for (the_element_id : new_element_ids.elements) {
      assert !element_ids.contains_key(the_element_id.short_name);
      element_ids.put(the_element_id.short_name, the_element_id);
    }
  }

  public void add_attributes(readonly collection[attribute_id] new_attribute_ids) {
    assert !is_completed();
    for (the_attribute_id : new_attribute_ids.elements) {
      assert !attribute_ids.contains_key(the_attribute_id.short_name);
      attribute_ids.put(the_attribute_id.short_name, the_attribute_id);
    }
  }

  public void add_entities(readonly collection[special_text] new_entities) {
    assert !is_completed();
    for (the_entity : new_entities.elements) {
      assert !entities.contains_key(the_entity.name);
      entities.put(the_entity.name, the_entity);
    }
  }

  public void complete() {
    assert !is_completed();
    document_matcher = document();
  }

  protected boolean name_start(character c) pure {
    return the_character_handler.is_letter(c) || c == '_' || c == ':';
  }

  protected boolean name_char(character c) pure {
    return the_character_handler.is_letter(c) || c == '.' || c == '-' || c == '_' || c == ':';
  }

  protected boolean content_char(character c) pure {
    return c != '<' && c != '&';
  }

  protected boolean content_not_apos(character c) pure {
    return c != '<' && c != '&' && c != '\'';
  }

  protected boolean content_not_quot(character c) pure {
    return c != '<' && c != '&' && c != '"';
  }

  pattern[character] one(function[boolean, character] the_predicate) pure {
    return predicate_pattern[character].new(the_predicate);
  }

  pattern[character] one_character(character the_character) pure {
    return singleton_pattern[character].new(the_character);
  }

  pattern[character] zero_or_more(function[boolean, character] the_predicate) pure {
    return repeat_element[character].new(the_predicate, true);
  }

  pattern[character] one_or_more(function[boolean, character] the_predicate) pure {
    return repeat_element[character].new(the_predicate, false);
  }

  pattern[character] repeat_or_none(pattern[character] the_pattern) pure {
    return repeat_pattern[character].new(the_pattern, true);
  }

  -- TODO: return sequence_pattern[character] (which breaks list initializer.)
  pattern[character] sequence(readonly list[pattern[character]] patterns_list) pure {
    return sequence_pattern[character].new(patterns_list);
  }

  option_pattern[character] option(readonly list[pattern[character]] patterns_list) pure {
    return option_pattern[character].new(patterns_list);
  }

  option_matcher[character, attribute_fragment] option_fragment_list(
      readonly list[matcher[character, attribute_fragment]] matchers) pure {
    return option_matcher[character, attribute_fragment].new(matchers);
  }

  option_matcher[character, text_fragment] option_text_fragment(
      readonly list[matcher[character, text_fragment]] matchers) pure {
    return option_matcher[character, text_fragment].new(matchers);
  }

  option_matcher[character, text_element] option_text_element(
      readonly list[matcher[character, text_element]] matchers) pure {
    return option_matcher[character, text_element].new(matchers);
  }

  -- TODO: the conversions should be inferred.
  option_matcher[character, attribute_fragment] option_fragment(
      matcher[character, string] attr_value,
      matcher[character, special_text] entity_ref) pure {
    return option_fragment_list([
        entity_ref !> matcher[character, attribute_fragment],
        attr_value !> matcher[character, attribute_fragment],
    ]);
  }

  -- TODO: wrapper function is redundant
  attribute_fragment join_fragments(readonly list[attribute_fragment] fragments) pure {
    return text_util.join_attributes(fragments);
  }

  matcher[character, attribute_fragment] repeat_or_none_fragment(
      matcher[character, attribute_fragment] the_matcher) pure {
    return repeat_matcher[character, attribute_fragment, attribute_fragment].new(the_matcher, true,
        join_fragments);
  }

  -- TODO: wrapper function is redundant
  text_fragment join_fragments_text(readonly list[text_fragment] fragments) pure {
    return text_util.join(fragments);
  }

  matcher[character, text_fragment] repeat_or_none_text(
      matcher[character, text_fragment] the_matcher) pure {
    return repeat_matcher[character, text_fragment, text_fragment].new(the_matcher, true,
        join_fragments_text);
  }

  immutable list[attribute_state] cast_attributes(readonly list[attribute_state] attributes) pure {
    return attributes.elements !> immutable list[attribute_state];
  }

  matcher[character, immutable list[attribute_state]] repeat_or_none_attribute(
      matcher[character, attribute_state] the_matcher) pure {
    return repeat_matcher[character, immutable list[attribute_state], attribute_state].new(
        the_matcher, true, cast_attributes);
  }

  string as_string_procedure(readonly list[character] the_character_list) pure {
    return the_character_list.frozen_copy() !> base_string;
  }

  matcher[character, string] as_string(pattern[character] the_pattern) pure {
    return procedure_matcher[character, string].new(the_pattern, as_string_procedure);
  }

  text_element match_start_element(readonly list[any value] the_list) pure {
    element_name : the_list[1] !> string;
    attributes : the_list[2] !> immutable list[attribute_state];

    element_id : element_ids.get(element_name);
    if (element_id is null) {
      parser.report_error("Unrecognized element name: " ++ element_name);
      return base_element.new(text_library.ERROR_ELEMENT);
    }

    dictionary[attribute_id, attribute_fragment] attributes_dictionary :
        list_dictionary[attribute_id, attribute_fragment].new();
    for (attribute : attributes) {
      attributes_dictionary.put(attribute.id, attribute.value);
    }

    return base_element.new(element_id, attributes_dictionary, missing.instance);
  }

  text_element match_text_element(readonly list[any value] the_list) pure {
    start_tag : the_list[0] !> text_element;
    text_content : the_list[1] !> text_fragment;
    end_tag_name : the_list[2] !> string;

    start_tag_name : start_tag.get_id.short_name;
    if (start_tag.get_id != text_library.ERROR_ELEMENT && start_tag_name != end_tag_name) {
      parser.report_error("Mismatched element name: start " ++ start_tag_name ++ ", end " ++
          end_tag_name);
    }

    return base_element.new(start_tag.get_id, start_tag.attributes, text_content);
  }

  special_text make_entity_2nd(readonly list[any value] the_list) pure {
    string entity_name : the_list[1] !> string;
    entity : entities.get(entity_name);

    if (entity is null) {
      parser.report_error("Unrecognized entity: " ++ entity_name);
      return text_library.ERROR_ENTITY;
    }

    return entity;
  }

  attribute_state make_attribute(readonly list[any value] the_list) pure {
    string attribute_name : the_list[0] !> string;
    attribute_fragment value : the_list[2] !> attribute_fragment;
    id : attribute_ids.get(attribute_name);
    if (id is null) {
      parser.report_error("Unrecognized attribute name: " ++ attribute_name);
      return attribute_state.new(text_library.ERROR_ATTRIBUTE, value);
    }
    return attribute_state.new(id, value);
  }

  string select_end_tag(readonly list[any value] the_list) pure => the_list[2] !> string;

  attribute_fragment select_2nd_attribute_fragment(readonly list[any value] the_list) pure =>
      the_list[1] !> attribute_fragment;

  attribute_state select_2nd_attribute_state(readonly list[any value] the_list) pure =>
      the_list[1] !> attribute_state;

  text_element select_2nd_text_element(readonly list[any value] the_list) pure =>
      the_list[1] !> text_element;

  text_fragment join2(readonly list[any value] the_list) pure {
    assert the_list.size == 2;
    return text_util.join(the_list[0] !> text_fragment, the_list[1] !> text_fragment);
  }

  protected matcher[character, text_element] document() {
    lt : one_character('<');
    gt : one_character('>');
    slash : one_character('/');
    amp : one_character('&');
    semicolon : one_character(';');
    quot : one_character('"');
    apos : one_character('\'');
    eq : one_character('=');

    space_opt : zero_or_more(the_character_handler.is_whitespace);
    name : as_string(sequence_pattern[character].new([ one(name_start), zero_or_more(name_char) ]));

    entity_ref = sequence_matcher[character, special_text].new([ amp, name, semicolon ],
        make_entity_2nd);

    equals : sequence([ space_opt, eq, space_opt ]);

    quot_attr_value = as_string(one_or_more(content_not_quot));
    attribute_value_in_quot = sequence_matcher[character, attribute_fragment].new([ quot,
        repeat_or_none_fragment(option_fragment(quot_attr_value, entity_ref)), quot ],
        select_2nd_attribute_fragment);

    apos_attr_value = as_string(one_or_more(content_not_apos));
    attribute_value_in_apos = sequence_matcher[character, attribute_fragment].new([ apos,
        repeat_or_none_fragment(option_fragment(apos_attr_value, entity_ref)), apos ],
        select_2nd_attribute_fragment);

    attribute_value : option_fragment_list([ attribute_value_in_quot, attribute_value_in_apos ]);
    attribute = sequence_matcher[character, attribute_state].new(
        [ name, equals, attribute_value ], make_attribute);
    attributes : repeat_or_none_attribute(
        sequence_matcher[character, attribute_state].new([ space_opt, attribute ],
            select_2nd_attribute_state));

    empty_element = sequence_matcher[character, text_element].new(
        [ lt, name, attributes, space_opt, slash, gt ],
        match_start_element);
    element : option_text_element([empty_element, ]);
    element_fragment : element !> matcher[character, text_fragment];
    content_element : option_text_fragment([element_fragment,
        entity_ref !> matcher[character, text_fragment]]);
    char_data_opt : as_string(zero_or_more(content_char));
    content_tail : repeat_or_none_text(sequence_matcher[character, text_fragment].new(
        [ content_element .> pattern[character], char_data_opt ], join2));
    content = sequence_matcher[character, text_fragment].new([ char_data_opt,
        content_tail .> pattern[character]], join2);

    pattern[character] start_tag : sequence_matcher[character, text_element].new(
        [ lt, name, attributes, space_opt, gt ], match_start_element);
    end_tag : sequence_matcher[character, string].new([ lt, slash, name, space_opt, gt ],
        select_end_tag);
    element.add_option(sequence_matcher[character, text_element].new(
        [ start_tag, content, end_tag ], match_text_element));

    result : sequence_matcher[character, text_element].new([ space_opt, element, space_opt ],
        select_2nd_text_element);

    update_matchers();

    result.validate();

    return result;
  }

  void update_matchers() {
  }

  text_element parse(string text, markup_parser parser) {
    this.parser = parser;
    return document_matcher.parse(text);
  }
}

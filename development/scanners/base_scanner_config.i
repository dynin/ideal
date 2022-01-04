-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class base_scanner_config {
  implements scanner_config;

  private keyword_dictionary : hash_dictionary[simple_name, token_matcher].new();
  private elements_list : base_list[scanner_element].new();

  override character_handler the_character_handler => unicode_handler.instance;

  override boolean is_whitespace(the character) {
    return the_character_handler.is_whitespace(the_character);
  }

  override boolean is_name_start(the character) {
    return the_character_handler.is_letter(the_character) || the_character == '_';
  }

  override boolean is_name_part(the character) {
    return the_character_handler.is_letter_or_digit(the_character) || the_character == '_';
  }

  override readonly list[scanner_element] elements => elements_list;

  readonly list[token[deeply_immutable data]] scan(source_content source) {
    return scanner_engine.new(this).scan(source);
  }

  override token[deeply_immutable data] process_token(token[deeply_immutable data] the_token) {
    if (the_token.type == special_token_type.SIMPLE_NAME) {
      the_name : (the_token !> token[simple_name]).payload;
      matcher : keyword_dictionary.get(the_name);
      if (matcher is_not null) {
        the_origin : the_token.deeper_origin;
        assert the_origin is_not null;
        return matcher.process(the_origin);
      }
    }
    return the_token;
  }

  add(scanner_element element) {
    assert element is base_scanner_element;
    element.set_config(this);
    elements_list.append(element);
  }

  private do_add_keyword(simple_name name, token_matcher matcher) {
    old : keyword_dictionary.put(name, matcher);
    assert old is null;
  }

  add_keyword(the keyword) {
    do_add_keyword(simple_name.make(the_keyword.name),
        base_token_matcher[keyword].new(the_keyword, the_keyword));
  }

  add_punctuation(the punctuation_type) {
    add(punctuation_element.new(the_punctuation_type));
  }

  add_special(the special_name, the token_type) {
    do_add_keyword(simple_name.make(the_token_type.name),
        base_token_matcher[special_name].new(special_token_type.SPECIAL_NAME, the_special_name));
  }

  add_kind(the kind) {
    do_add_keyword(the_kind.name, base_token_matcher[kind].new(special_token_type.KIND, the_kind));
  }

  add_subtype_tag(subtype_tag tag) {
    do_add_keyword(tag.name,
        base_token_matcher[subtype_tag].new(special_token_type.SUBTYPE_TAG, tag));
  }

  add_modifier(modifier_kind modifier) {
    do_add_keyword(modifier.name,
        base_token_matcher[modifier_kind].new(special_token_type.MODIFIER_KIND, modifier));
  }

  add_flavor(type_flavor flavor) {
    do_add_keyword(flavor.name,
        base_token_matcher[type_flavor].new(special_token_type.FLAVOR, flavor));
  }

  add_jump(jump_category jump) {
    do_add_keyword(jump.jump_name, base_token_matcher[jump_category].new(special_token_type.JUMP,
        jump));
  }

  add_constraint(constraint_category constraint) {
    do_add_keyword(constraint.constraint_name,
        base_token_matcher[constraint_category].new(special_token_type.CONSTRAINT, constraint));
  }

  override add_reserved(string reserved_word, var keyword or null the_keyword) {
    name : simple_name.make(reserved_word);
    if (the_keyword is null) {
      the_keyword = keywords.RESERVED;
    }
    do_add_keyword(name, base_token_matcher[keyword].new(keywords.RESERVED, the_keyword));
  }

  private interface token_matcher {
    token[deeply_immutable data] process(the origin);
  }

  private static class base_token_matcher[deeply_immutable data payload_type] {
    implements token_matcher;

    private token_type the_token_type;
    private payload_type payload;

    base_token_matcher(token_type the_token_type, payload_type payload) {
      verify payload is_not null;
      this.the_token_type = the_token_type;
      this.payload = payload;
    }

    override token[deeply_immutable data] process(the origin) {
      return base_token[payload_type].new(the_token_type, payload, the_origin);
    }
  }
}

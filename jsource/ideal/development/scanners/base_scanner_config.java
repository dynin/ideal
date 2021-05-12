/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.constructs.jump_category;
import ideal.development.constructs.constraint_category;
import ideal.development.names.*;
import ideal.development.origins.*;
import javax.annotation.Nullable;

public class base_scanner_config implements scanner_config {
  private dictionary<simple_name, token_matcher> keyword_dictionary =
      new hash_dictionary<simple_name, token_matcher>();

  private list<scanner_element> elements = new base_list<scanner_element>();

  @Override
  public boolean is_whitespace(char c) {
    return Character.isWhitespace(c);
  }

  @Override
  public boolean is_name_start(char c) {
    return Character.isJavaIdentifierStart(c);
  }

  @Override
  public boolean is_name_part(char c) {
    return Character.isJavaIdentifierPart(c);
  }

  @Override
  public readonly_list<scanner_element> elements() {
    return elements;
  }

  public readonly_list<token> scan(source_content source) {
    return new scanner_engine(this).scan(source);
  }

  @Override
  public token process_token(token the_token) {
    if (the_token.type() == special_token_type.SIMPLE_NAME) {
      simple_name the_name = ((token<simple_name>) the_token).payload();
      token_matcher matcher = keyword_dictionary.get(the_name);
      if (matcher != null) {
        return matcher.process(the_token.deeper_origin());
      }
    }
    return the_token;
  }

  public void add(scanner_element element) {
    elements.append(element);
  }

  private void add_keyword(simple_name name, token_matcher matcher) {
    token_matcher old = keyword_dictionary.put(name, matcher);
    assert old == null;
  }

  public void add_keyword(keyword the_keyword) {
    add_keyword(simple_name.make(the_keyword.name()),
        new token_matcher<keyword>(the_keyword, the_keyword));
  }

  public void add_punctuation(punctuation_type the_punctuation_type) {
    add(new punctuation_element(the_punctuation_type));
  }

  public void add_special(special_name the_special_name, token_type the_token_type) {
    add_keyword(simple_name.make(the_token_type.name()),
        new token_matcher<special_name>(special_token_type.SPECIAL_NAME, the_special_name));
  }

  public void add_kind(kind kind) {
    add_keyword(kind.name(), new token_matcher<kind>(special_token_type.KIND, kind));
  }

  public void add_subtype_tag(subtype_tag tag) {
    add_keyword(tag.name(),
        new token_matcher<subtype_tag>(special_token_type.SUBTYPE_TAG, tag));
  }

  public void add_modifier(modifier_kind modifier) {
    add_keyword(modifier.name(),
        new token_matcher<modifier_kind>(special_token_type.MODIFIER_KIND, modifier));
  }

  public void add_flavor(type_flavor flavor) {
    add_keyword(flavor.name(), new token_matcher<type_flavor>(special_token_type.FLAVOR, flavor));
  }

  public void add_jump(jump_category jump) {
    add_keyword(jump.jump_name(), new token_matcher<jump_category>(special_token_type.JUMP, jump));
  }

  public void add_constraint(constraint_category constraint) {
    add_keyword(constraint.constraint_name(),
        new token_matcher<constraint_category>(special_token_type.CONSTRAINT, constraint));
  }

  @Override
  public void add_reserved(string reserved_word, @Nullable keyword the_keyword) {
    simple_name name = simple_name.make(reserved_word);
    if (the_keyword == null) {
      the_keyword = keywords.RESERVED;
    }
    add_keyword(name, new token_matcher<keyword>(keywords.RESERVED, the_keyword));
  }

  private static class token_matcher<P extends deeply_immutable_data> {
    private token_type type;
    private P payload;

    public token_matcher(token_type type, P payload) {
      assert payload != null;
      this.type = type;
      this.payload = payload;
    }

    public token<P> process(origin pos) {
      return new base_token<P>(type, payload, pos);
    }
  }
}

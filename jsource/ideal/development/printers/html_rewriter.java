/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.patterns.*;
import ideal.development.elements.*;
import ideal.development.documenters.*;

public class html_rewriter extends text_rewriter {

  private naming_strategy the_naming_strategy;
  private @Nullable text_element title_element;

  private final static pattern<Character> SCHEME_SEPARATOR = new singleton_pattern(':');
  private final static string IDEAL_SCHEME = new base_string("ideal");
  private final static pattern<Character> DOT_PATTERN = new singleton_pattern('.');

  public html_rewriter(naming_strategy the_naming_strategy) {
    this.the_naming_strategy = the_naming_strategy;
  }

  public @Nullable text_element get_title() {
    return title_element;
  }

  @Override
  protected text_fragment rewrite_element(element_id id,
      immutable_dictionary<attribute_id, attribute_fragment> attributes,
      @Nullable text_fragment children) {

    if (id == text_library.INDENT) {
      return styles.wrap(styles.indent_style, children);
    } else if (id == text_library.TITLE) {
      title_element = new base_element(id, attributes, children);
      return text_util.EMPTY_FRAGMENT;
    } else if (id == text_library.A) {
      return new base_element(id, rewrite_ideal_href(attributes), children);
    } else if (id.get_namespace() == doc_elements.DOC_NS) {
      if (id == doc_elements.CODE) {
        return styles.wrap(styles.code_style, children);
      } else {
        return styles.wrap(styles.documentation_note_style, children);
      }
    } else if (id == text_library.DIV && attributes.is_empty() && is_div(children)) {
      return children;
    } else {
      return new base_element(id, attributes, children);
    }
  }

  private static boolean is_div(text_fragment fragment) {
    return fragment instanceof text_element &&
        ((text_element) fragment).get_id() == text_library.DIV;
  }

  private readonly_dictionary<attribute_id, attribute_fragment> rewrite_ideal_href(
      immutable_dictionary<attribute_id, attribute_fragment> attributes) {
    dictionary<attribute_id, attribute_fragment> result =
        new list_dictionary<attribute_id, attribute_fragment>();
    readonly_list<dictionary.entry<attribute_id, attribute_fragment>> attribute_list =
        attributes.elements();

    for (int i = 0; i < attribute_list.size(); ++i) {
      dictionary.entry<attribute_id, attribute_fragment> attribute = attribute_list.get(i);
      attribute_id the_attribute_id = attribute.key();
      attribute_fragment the_attribute_value = attribute.value();
      if (the_attribute_id == text_library.HREF &&
          the_attribute_value instanceof base_string) {
        the_attribute_value = rewrite_ideal_scheme((base_string) the_attribute_value);
      }
      result.put(the_attribute_id, the_attribute_value);
    }

    return result;
  }

  private base_string rewrite_ideal_scheme(base_string href) {
    @Nullable range separator = SCHEME_SEPARATOR.find_first(href, 0);
    if (separator != null) {
      // TODO: make case-insensitive.
      if (utilities.eq(href.slice(0, separator.begin()), IDEAL_SCHEME)) {
        string type_name = href.skip(separator.end());
        immutable_list<immutable_list<Character>> names = DOT_PATTERN.split(type_name);
        list<simple_name> target_name = new base_list<simple_name>();
        for (int i = 0; i < names.size(); ++i) {
          // TODO: introduce a sanity check.
          target_name.append(simple_name.make((base_string) names.get(i)));
        }
        return the_naming_strategy.link_to_resource(target_name,
            the_naming_strategy.default_extension());
      }
    }
    return href;
  }
}

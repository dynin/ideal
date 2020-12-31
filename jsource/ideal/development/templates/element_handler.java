/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.templates;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import static ideal.runtime.texts.markup_formatter.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.extensions.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

/**
 * The interface that hides the differences between element and attribute ids was
 * proposed by Erik Naggum.
 *
 * @see http://genius.cat-v.org/erik-naggum/lisp-markup
 */
public class element_handler implements sexpression_handler {
  private final element_id element;

  public element_handler(element_id element) {
    this.element = element;
  }

  public action_name name() {
    return simple_name.make(element.short_name());
  }

  public analyzable to_analyzable(readonly_list<construct> arguments, template_analyzer template,
      origin source) {

    if (arguments.is_empty()) {
      // The simple case of self-closing tag
      string tag = new base_string(OPEN_START_TAG, element.short_name(), CLOSE_SELF_CLOSING_TAG);
      return template.make_appender(tag, source);
    }

    statement_list_analyzer result = new statement_list_analyzer(source);

    list<analyzable> subactions = new base_list<analyzable>();
    string start_tag = new base_string(OPEN_START_TAG, element.short_name());
    subactions.append(template.make_appender(start_tag, result));

    boolean in_attributes = true;

    for (int i = 0; i < arguments.size(); ++i) {
      construct arg_construct = arguments.get(i);
      analyzable arg = template.process_template_expression(arg_construct);

      if (arg instanceof error_signal) {
        subactions.append(arg);
        continue;
      }

      if (template.is_attribute_expression(arg_construct)) {
        if (!in_attributes) {
          new error_signal(new base_string("Attribute not expected here"), arg);
        }
        subactions.append(arg);
      } else {
        if (in_attributes) {
          subactions.append(template.make_appender(CLOSE_TAG, result));
          in_attributes = false;
        }
        subactions.append(arg);
      }
    }

    if (in_attributes) {
      subactions.append(template.make_appender(CLOSE_SELF_CLOSING_TAG, result));
    } else {
      string end_tag = new base_string(OPEN_END_TAG, element.short_name(), CLOSE_TAG);
      subactions.append(template.make_appender(end_tag, result));
    }

    result.populate(subactions);
    return result;
  }
}

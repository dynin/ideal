/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import static ideal.experiment.mini.bootstrapped.text;
import static ideal.experiment.mini.bootstrapped.text_string;
import static ideal.experiment.mini.bootstrapped.indented_text;
import static ideal.experiment.mini.bootstrapped.text_list;
import static ideal.experiment.mini.bootstrapped.describable;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class library {

  public interface function<result, argument> {
    result call(argument the_argument);
  }

  public interface predicate<argument> {
    boolean call(argument the_argument);
  }

  public static void panic(String message) {
    throw new Error(message);
  }

  public static <source_type, target_type> List<target_type> map(
      List<? extends source_type> source_list,
      function<target_type, source_type> transform) {
    List<target_type> result = new ArrayList<target_type>();
    for (source_type source_element : source_list) {
      result.add(transform.call(source_element));
    }
    return result;
  }

  public static <element_type> List<element_type> filter(List<? extends element_type> source_list,
      predicate<element_type> the_predicate) {
    List<element_type> result = new ArrayList<element_type>();
    for (element_type source_element : source_list) {
      if (the_predicate.call(source_element)) {
        result.add(source_element);
      }
    }
    return result;
  }

  public static <argument> predicate<argument> negate_predicate(
      final predicate<argument> the_predicate) {
    return new predicate<argument>() {
      @Override
      public boolean call(argument the_argument) {
        return !the_predicate.call(the_argument);
      }
    };
  }

  public static boolean is_letter(char c) {
    return Character.isLetter(c);
  }

  public static boolean is_whitespace(char c) {
    return Character.isWhitespace(c);
  }

  public static String to_lower_case(String s) {
    return s.toLowerCase();
  }

  public static String describe_type(Object the_object) {
    String name = the_object.getClass().getName();
    int dollar_index = name.lastIndexOf('$');
    if (dollar_index >= 0) {
      name = name.substring(dollar_index + 1);
    }
    return name;
  }

  public static text join_text(text... texts) {
    return new text_list(Arrays.asList(texts));
  }

  public static text indent(text... texts) {
    // TODO: optimize.
    return new indented_text(new text_list(Arrays.asList(texts)));
  }

  // Parameters must be either text ot strings
  public static text join_fragments(Object... fragments) {
    List<text> result = new ArrayList<text>();
    for (Object the_fragment : fragments) {
      if (the_fragment instanceof String) {
        result.add(new text_string((String) the_fragment));
      } else {
        result.add((text) the_fragment);
      }
    }
    return new text_list(result);
  }

  public static text join_text(List<text> texts, text separator) {
    List<text> result = new ArrayList<text>();
    for (int i = 0; i < texts.size(); ++i) {
      if (i > 0) {
        result.add(separator);
      }
      result.add(texts.get(i));
    }
    return new text_list(result);
  }

  public static text join_with_terminator(List<text> texts, text terminator) {
    List<text> result = new ArrayList<text>();
    for (text the_text : texts) {
      result.add(the_text);
      result.add(terminator);
    }
    return new text_list(result);
  }

  public static String render_text(text the_text) {
    StringBuilder result = new StringBuilder();
    do_render_text(the_text, true, 0, result);
    return result.toString();
  }

  public static function<text, Object> describe_fn = new function<text, Object>() {
    @Override
    public text call(Object the_object) {
      return describe(the_object);
    }
  };

  // Describe an object; no trailing newline
  public static text describe(Object object) {
    if (object instanceof describable) {
      return ((describable) object).description();
    } else if (object == null) {
      return NULL_NAME;
    } else if (object instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> objects = (List<Object>) object;
      if (objects.isEmpty()) {
        return join_text(START_LIST, END_LIST);
      }
      text elements = new indented_text(join_with_terminator(map(objects, describe_fn), NEWLINE));
      return join_text(START_LIST, NEWLINE, elements, END_LIST);
    } else if (object instanceof String) {
      return join_fragments(STRING_QUOTE, (String) object, STRING_QUOTE);
    } else if (object instanceof Character) {
      return join_fragments(CHARACTER_QUOTE, ((Character) object).toString(), CHARACTER_QUOTE);
    } else {
      return new text_string(object.toString());
    }
  }

  public static String describe_s(Object object) {
    return render_text(describe(object));
  }

  public static text field_is(String name, Object value) {
    return join_fragments(name, FIELD_IS, describe(value), NEWLINE);
  }

  public static final text EMPTY_TEXT = new text_string("");
  public static final text SPACE = new text_string(" ");
  public static final text NEWLINE = new text_string("\n");

  public static final text NULL_NAME = new text_string("<null>");
  public static final text START_OBJECT = new text_string(" {");
  public static final text END_OBJECT = new text_string("}");
  public static final text START_LIST = new text_string("[");
  public static final text END_LIST = new text_string("]");
  public static final text FIELD_IS = new text_string(": ");
  public static final text STRING_QUOTE = new text_string("\"");
  public static final text CHARACTER_QUOTE = new text_string("'");

  private static final String INDENT_STRING = "  ";

  private static boolean do_render_text(text the_text, boolean first, int indent,
      StringBuilder result) {
    if (the_text instanceof text_string) {
      String value = ((text_string) the_text).value();
      for (int i = 0; i < value.length(); ++i) {
        char c = value.charAt(i);
        if (c != '\n') {
          if (first) {
            for (int j = 0; j < indent; ++j) {
              result.append(INDENT_STRING);
            }
          }
          result.append(c);
          first = false;
        } else {
          result.append('\n');
          first = true;
        }
      }
    } else if (the_text instanceof indented_text) {
      first = do_render_text(((indented_text) the_text).inside(), first, indent + 1, result);
    } else {
      assert the_text instanceof text_list;
      for (text sub_text : ((text_list)the_text).texts()) {
        first = do_render_text(sub_text, first, indent, result);
      }
    }

    return first;
  }
}

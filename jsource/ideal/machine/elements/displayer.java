/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.elements;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.machine.annotations.dont_display;
import ideal.machine.channels.string_writer;

import java.io.*;
import java.lang.reflect.*;

public class displayer {

  private static final base_string NULL_NAME = new base_string("<null>");
  private static final base_string START_OBJECT = new base_string("{");
  private static final base_string END_OBJECT = new base_string("}");
  private static final base_string START_LIST = new base_string("[");
  private static final base_string END_LIST = new base_string("]");
  private static final base_string SPACE = new base_string(" ");
  private static final base_string FIELD_IS = new base_string(": ");

  private static text_fragment display_field(Field f, readonly_value obj)
      throws IllegalAccessException {
    Object field = f.get(obj);

    if (field == null) {
      return NULL_NAME;
    }

    if (field instanceof readonly_displayable) {
      return (base_string) ((readonly_displayable) field).display();
    }

    Class type = field.getClass();
    if (any_entity.class.isAssignableFrom(type)) {
      if (field instanceof readonly_value) {
        return display_object((readonly_value) field);
      } else {
        utilities.panic("Unknown field: " + field);
        //return (base_string) utilities.describe(field);
      }
    }

    if (type == Boolean.TYPE || type == Boolean.class) {
      return new base_string(field.toString());
    } else if (type == Integer.TYPE || type == Integer.class) {
      return new base_string(((Integer) field).toString());
    } else if (type == Character.TYPE) {
      return make_literal(((Character) field).toString(), "'");
    } else if (type == String.class) {
      return make_literal((String) field, "\"");
    } else {
      throw new RuntimeException("Unknown class " + type + " in " + f);
    }
  }

  private static text_node display_enum_field(String name, Object value) {
    list<text_node> field = new base_list<text_node>();
    field.append(new base_string(name));
    field.append(FIELD_IS);
    if (value instanceof String) {
      field.append(make_literal((String) value, "\""));
    } else {
      field.append(new base_string(value.toString()));
    }
    return text_utilities.make_element(text_library.DIV, field);
  }

  static text_fragment display_object(readonly_value obj) {
    if (obj instanceof string) {
      return make_literal(utilities.s((string) obj), "\"");
    } else if (obj instanceof readonly_list) {
      return display_list((readonly_list) obj);
    } else if (obj instanceof readonly_displayable) {
      return (base_string) ((readonly_displayable) obj).display();
    }

    list<text_node> result = new base_list<text_node>();

    Class c = obj.getClass();
    append(result, (base_string) runtime_util.short_class_name(obj));
    append(result, SPACE);

    list<text_node> body = new base_list<text_node>();

    try {
      for (Field f : runtime_util.get_fields(c)) {
        if (!f.isAnnotationPresent(dont_display.class)) {
          list<text_node> field = new base_list<text_node>();
          field.append(new base_string(f.getName()));
          field.append(FIELD_IS);
          append(field, display_field(f, obj));
          body.append(text_utilities.make_element(text_library.DIV, field));
        }
      }
      if (obj instanceof Enum) {
        body.append(display_enum_field("name", ((Enum) obj).name()));
        body.append(display_enum_field("ordinal", ((Enum) obj).ordinal()));
      }
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    }

    append(result, make_aggregate(START_OBJECT, body, END_OBJECT));

    return new base_list_text_node(result);
  }

  private static void append(list<text_node> list, text_fragment fragment) {
    if (fragment instanceof list_text_node) {
      list.append_all(((list_text_node) fragment).nodes());
    } else {
      list.append((text_node) fragment);
    }
  }

  private static base_string make_literal(String s, String quote) {
    return new base_string(quote, s, quote);
  }

  private static text_fragment make_aggregate(base_string start,
      list<text_node> fragments, base_string end) {
    if (fragments.is_empty()) {
      return new base_string(start, SPACE, end);
    }

    list<text_node> result = new base_list<text_node>();

    result.append(start);
    text_element content = text_utilities.make_element(text_library.INDENT, fragments.frozen_copy());
    content = new base_element(text_library.DIV, content);
    result.append(content);
    result.append(end);

    return new base_list_text_node(result);
  }

  private static text_fragment display_list(readonly_list list) {
    list<text_node> result = new base_list<text_node>();

    for (int i = 0; i < list.size(); ++i) {
      Object obj = list.get(i);
      if (obj instanceof readonly_value) {
        append(result, new base_element(
            text_library.DIV, display_object((readonly_value) obj)));
      } else {
        throw new RuntimeException("Not data: " + obj);
      }
    }

    return make_aggregate(START_LIST, result, END_LIST);
  }
}

/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.elements;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.texts.text_fragment;
import ideal.runtime.elements.base_string;
import ideal.runtime.elements.readonly_has_equivalence;
import ideal.runtime.elements.utilities;
import ideal.runtime.texts.text_library;
import ideal.runtime.texts.base_element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Nullable;

public class runtime_util {

  private static final Map<Class, Field[]> cache = new HashMap<Class, Field[]>();

  public static string short_class_name(Object obj) {
    return main_name(obj.getClass().getName());
  }

  private static string main_name(String name) {
    int dot = name.lastIndexOf('.');
    if (dot > 0) {
      name = name.substring(dot + 1);
    }
    int dollar = name.lastIndexOf('$');
    if (dollar > 0) {
      name = name.substring(dollar + 1);
    }
    return new base_string(name);
  }

  public static string string_of(Object the_value) {
    return new base_string(String.valueOf(the_value));
  }

  public static string value_identifier(Object the_value) {
    return new base_string(short_class_name(the_value), "@",
        Integer.toHexString(System.identityHashCode(the_value)));
  }

  public static Field[] get_fields(final Class c) {
    Field[] result = cache.get(c);

    if (result == null) {
      boolean is_enum = c.isEnum();
      ArrayList<Field> fields = new ArrayList<Field>();
      Class current = c;

      do {
        for (Field f : current.getDeclaredFields()) {
          int modifiers = f.getModifiers();

          if ((modifiers & Modifier.STATIC) != 0) {
            continue;
          }

          if (is_enum) {
            String name = f.getName();
            if (name.equals("name") || name.equals("ordinal")) {
              continue;
            }
          }

          f.setAccessible(true);
          fields.add(f);
        }

        current = current.getSuperclass();
      } while (current != null);

      result = new Field[fields.size()];
      fields.toArray(result);
      cache.put(c, result);
    }

    return result;
  }

  public static int compute_hash_code(Object d) {
    if (d instanceof Integer) {
      return ((Integer) d).hashCode();
    }

    if (d instanceof readonly_reference_equality) {
      return System.identityHashCode(d);
    }

    if (d instanceof base_string) {
      return ((base_string) d).s().hashCode();
    }

    if (d instanceof readonly_has_equivalence) {
      readonly_has_equivalence dd = (readonly_has_equivalence) d;
      equivalence_relation<readonly_has_equivalence> equivalence = dd.equivalence();
      if (equivalence instanceof equivalence_with_hash) {
        return ((equivalence_with_hash<readonly_has_equivalence>) equivalence).hash(dd);
      }
    }

    if (d instanceof immutable_list) {
      return compute_hash_code_list((immutable_list<readonly_data>) d);
    } else {
      return compute_hash_code_composite((readonly_value) d);
    }
  }

  private static int compute_hash_code_list(immutable_list<readonly_data> the_list) {
    int result = 20;

    for (int i = 0; i < the_list.size(); ++i) {
      result = result * 31 + compute_hash_code(the_list.get(i));
    }

    return result;
  }

  private static int compute_hash_code_composite(readonly_value d) {
    Field[] fields = get_fields(d.getClass());
    int result = 10;

    for (int i = 0; i < fields.length; ++i) {
      Field f = fields[i];
      Object val = null;
      int hash;
      try {
        val = f.get(d);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      if (val == null) {
        hash = 42;
      } else if (val instanceof readonly_value) {
        hash = compute_hash_code((readonly_value) val);
      } else {
        //utilities.panic("compute_hash_code() for " + val.getClass());
        hash = val.hashCode();
      }
      result = result * 31 + hash;
    }

    return result;
  }

  public static boolean data_equals(Object d1, Object d2) {
    if (d1 == d2) {
      return true;
    }

    if (d1.getClass() != d2.getClass()) {
      // TODO: handle list vs. empty
      return false;
    }

    if (d1 instanceof Enum || d1 instanceof readonly_reference_equality) {
      return d1 == d2;
    }

    if (d1 instanceof base_string) {
      return ((base_string) d1).s().equals(((base_string) d2).s());
    }

    if (d1 instanceof readonly_has_equivalence) {
      readonly_has_equivalence e1 = (readonly_has_equivalence) d1;
      readonly_has_equivalence e2 = (readonly_has_equivalence) d2;
      return e1.equivalence().call(e1, e2);
    }

    if (d1 instanceof Integer) {
      return d1.equals(d2);
    }

    if (d1 instanceof immutable_list) {
      @SuppressWarnings("unchecked")
      immutable_list<readonly_data> list1 = (immutable_list<readonly_data>) d1;
      @SuppressWarnings("unchecked")
      immutable_list<readonly_data> list2 = (immutable_list<readonly_data>) d2;

      if (list1.size() != list2.size()) {
        return false;
      }

      for (int i = 0; i < list1.size(); ++i) {
        if (!data_equals(list1.get(i), list2.get(i))) {
          return false;
        }
      }

      return true;
    }

    Field[] fields = get_fields(d1.getClass());

    for (int i = 0; i < fields.length; ++i) {
      Field f = fields[i];
      try {
        Object val1 = f.get(d1);
        Object val2 = f.get(d2);
        if (!values_equal(val1, val2)) {
          return false;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return true;
  }

  public static equivalence_with_hash<Object> default_equivalence = new default_equivalence_impl();

  private static class default_equivalence_impl implements equivalence_with_hash<Object> {
    @Override
    public Boolean call(Object first, Object second) {
      return runtime_util.values_equal(first, second);
    }

    public Integer hash(Object the_value) {
      return runtime_util.compute_hash_code(the_value);
    }
  };

  public static boolean values_equal(Object o1, Object o2) {
    if (o1 == o2) {
      return true;
    }
    if (o1 == null || o2 == null) {
      return false;
    }
    if (o1 instanceof readonly_value) {
      return data_equals((readonly_value) o1, o2);
    }
    return o1.equals(o2);
  }

  private static final string NULL_STRING = new base_string("null");

  private static string to_string(@Nullable Object the_object) {
    if (the_object == null) {
      return NULL_STRING;
    } else if (the_object instanceof readonly_stringable) {
      return ((readonly_stringable) the_object).to_string();
    } else {
      return new base_string(the_object.toString());
    }
  }

  public static string concatenate(Object o1, Object o2) {
    return new base_string(to_string(o1), to_string(o2));
  }

  public static sign compare(Object first, Object second) {
    // Handle only Integers for now.
    assert first instanceof Integer && second instanceof Integer;
    int first_int = (Integer) first;
    int second_int = (Integer) second;

    if (first_int < second_int) {
      return sign.less;
    } else if (first_int == second_int) {
      return sign.equal;
    } else {
      return sign.greater;
    }
  }

  public static void do_panic(String message) {
    System.err.println("PANIC: " + message);
    print_stack();

    try {
      System.exit(1);
    } catch (Throwable t) {
      // We are running as a servlet, can't exit
      throw new RuntimeException(message);
    }
  }

  private static final int SKIP_STACK_FRAMES = 4;

  private static void print_stack() {
    StackTraceElement[] stack_trace = Thread.currentThread().getStackTrace();
    for (int i = SKIP_STACK_FRAMES; i < stack_trace.length; ++i) {
      StackTraceElement element = stack_trace[i];
      System.err.println("       " + runtime_util.main_name(element.getClassName()) + "." +
          element.getMethodName() + "() in " + element.getFileName() + ":" +
          element.getLineNumber());
    }
    System.err.flush();
  }

  public static void do_stack(String message) {
    System.err.println("TRACE: " + message);
    print_stack();
  }

  // TODO: optimize this; add HTML entity declarations.
  public static string escape_markup(string the_string) {
    StringBuilder sb = new StringBuilder();
    String unwrapped = utilities.s(the_string);

    for (int i = 0; i < unwrapped.length(); ++i) {
      char c = unwrapped.charAt(i);

      switch (c) {
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\'':
          sb.append("&apos;");
          break;
        case '"':
          sb.append("&quot;");
          break;
        default:
          sb.append(c);
          break;
      }
    }

    return new base_string(sb.toString());
  }

  public static text_fragment display(readonly_value obj) {
    return new base_element(text_library.DIV, displayer.display_object(obj));
  }

  public static void start_test(string name) {
    System.err.print(name + "... ");
    System.err.flush();
  }

  public static void end_test() {
    System.err.println("ok");
    System.err.flush();
  }

  public static string int_to_string(int int_value) {
    return new base_string(String.valueOf(int_value));
  }
}

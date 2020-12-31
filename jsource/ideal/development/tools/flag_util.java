/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

import java.lang.reflect.*;

public class flag_util {

  public static void parse_flags(String[] args, Object options) {
    dictionary<string, string> argmap = new hash_dictionary<string, string>();
    int index;

    for (index = 0; index < args.length; ++index) {
      String arg = args[index];
      if (arg.charAt(0) == '-') {
        int end = arg.indexOf('=');
        if (end == -1) {
          end = arg.indexOf(':');
        }
        if (end == -1) {
          argmap.put(normalize(arg.substring(1)), new base_string(""));
        } else {
          argmap.put(normalize(arg.substring(1, end)), new base_string(arg.substring(end + 1)));
        }
      } else {
        break;
      }
    }

    if (index < args.length) {
      throw new RuntimeException("non-flag parameters--don't know what to do!");
    }

    try {
      Class cl = options.getClass();
      boolean has_flag = false;
      for (Field f : cl.getDeclaredFields()) {
        assert (f.getModifiers() & Modifier.STATIC) == 0;
        f.setAccessible(true);
        has_flag = true;
        if (f.getType() == Boolean.TYPE) {
          f.setBoolean(options, boolean_flag(argmap, f.getName()));
        } else if (f.getType() == string.class) {
          f.set(options, string_flag(argmap, f.getName()));
        } else {
          throw new RuntimeException("Strange flag " + f.getName());
        }
      }
      if (!has_flag) {
        throw new RuntimeException(cl.getName() + " has no flags");
      }
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException(e);
      }
    }

    if (argmap.is_not_empty()) {
      throw new RuntimeException("Unknown flag: " + argmap.keys().elements().first());
    }
  }

  private static string normalize(String s) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c != '-' && c != '_') {
        result.append(Character.toLowerCase(c));
      }
    }
    return new base_string(result.toString());
  }

  private static boolean boolean_flag(dictionary<string, string> argmap, String name) {
    string flag_name = normalize(name);

    if (argmap.get(new base_string("not", flag_name)) != null) {
      argmap.remove(new base_string("not", flag_name));
      return false;
    }

    if (argmap.get(new base_string("no", flag_name)) != null) {
      argmap.remove(new base_string("no", flag_name));
      return false;
    }

    string value = argmap.get(flag_name);
    if (value == null) {
      return false;
    }

    string flag_value = normalize(utilities.s(value));
    argmap.remove(flag_name);
    return !utilities.eq(flag_value, new base_string("false")) &&
        !utilities.eq(flag_value, new base_string("no"));
  }

  private static @Nullable string string_flag(dictionary<string, string> argmap, String name) {
    string flag_name = normalize(name);
    string flag_value = argmap.get(flag_name);

    if (flag_value != null) {
      return argmap.remove(flag_name);
    } else {
      return null;
    }
  }
}

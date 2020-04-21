/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class name_utilities {

  // do not instantiate
  private name_utilities() { }

  private static simple_name FIRST = simple_name.make("first");
  private static simple_name SECOND = simple_name.make("second");
  private static simple_name THIRD = simple_name.make("third");

  public static string in_brackets(readonly_stringable name) {
    return new base_string("<", name.to_string(), ">");
  }

  public static simple_name make_numbered_name(int index) {
    switch (index) {
      case 0:
        return FIRST;
      case 1:
        return SECOND;
      case 2:
        return THIRD;
      default:
        utilities.panic("Don't know how to count up to " + index);
        return null;
    }
  }

  public static simple_name join(simple_name first, simple_name second) {
    list<string> segments = new base_list<string>();

    segments.append_all(first.segments);
    segments.append_all(second.segments);

    return simple_name.make_from_segments(segments.frozen_copy());
  }

  public static simple_name parse_camel_case(String name) {
    list<string> segments = new base_list<string>();
    int index = 0;

    do {
      StringBuilder sb = new StringBuilder();

      while (index < name.length() && Character.isUpperCase(name.charAt(index))) {
        sb.append(Character.toLowerCase(name.charAt(index)));
        ++index;
      }

      while (index < name.length() && !Character.isUpperCase(name.charAt(index))) {
        sb.append(name.charAt(index));
        ++index;
      }

      segments.append(new base_string(sb.toString()));
    } while (index < name.length());

    if (!segments.is_empty()) {
      return simple_name.make_from_segments(segments.frozen_copy());
    } else {
      utilities.panic("Can't parse name " + name);
      return null;
    }
  }
}

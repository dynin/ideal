/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.elements;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;

public class simple_name extends debuggable implements action_name, readonly_displayable {

  private static final dictionary<immutable_list<string>, simple_name> all_names =
    new hash_dictionary<immutable_list<string>, simple_name>();

  public final immutable_list<string> segments;

  private simple_name(immutable_list<string> segments) {
    this.segments = segments;
  }

  @Override
  public base_string to_string() {
    if (segments.size() == 1) {
      return (base_string) segments.first();
    } else {
      StringBuilder s = new StringBuilder();
      for (int i = 0; i < segments.size(); ++i) {
        s.append(utilities.s(segments.get(i)));
        if (i != segments.size() - 1) {
          s.append("_");
        }
      }
      // TODO: memoize this?
      return new base_string(s.toString());
    }
  }

  @Override
  public string display() {
    return to_string();
  }

  public static simple_name make_from_segments(immutable_list<string> segments) {
    assert !segments.is_empty();
    simple_name result = all_names.get(segments);

    if (result == null) {
      result = new simple_name(segments);
      all_names.put(segments, result);
    }

    return result;
  }

  public static simple_name make(String name) {
    assert name.length() > 0;

    list<string> segments = new base_list<string>();
    int index = 0;

    while (true) {
      int underscore = name.indexOf('_', index);
      if (underscore < 0) {
        segments.append(new base_string(name.substring(index)));
        break;
      }
      segments.append(new base_string(name.substring(index, underscore)));
      index = underscore + 1;
    }

    return make_from_segments(segments.frozen_copy());
  }

  public static simple_name make(string name) {
    return make(utilities.s(name));
  }
}

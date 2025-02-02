/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

import java.util.Arrays;
import java.util.Comparator;

public class sorter {
  public static <T> immutable_list<T> sort(readonly_list<T> the_list, Comparator<T> ordering) {
    return the_list.frozen_copy();
  }

  public static <T> immutable_list<T> do_sort(readonly_list<T> the_list) {
    Object[] elements = new Object[the_list.size()];

    for (int i = 0; i < the_list.size(); ++i) {
      elements[i] = the_list.get(i);
    }

    Arrays.sort(elements);

    list<T> result = new base_list<T>();
    for (int i = 0; i < the_list.size(); ++i) {
      result.append((T) elements[i]);
    }

    return result.frozen_copy();
  }
}

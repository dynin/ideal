/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class sorter {
  public static <T> immutable_list<T> sort(readonly_list<T> the_list, Comparator<T> ordering) {
    return the_list.frozen_copy();
  }
}

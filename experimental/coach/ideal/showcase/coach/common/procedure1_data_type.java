/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.showcase.coach.reflections.*;
public class procedure1_data_type extends procedure1<data_type> {
  public procedure1_data_type(String s) {
    super(s, data_type.class);
  }

  @Override
  protected string to_string(data_type the_type) {
    return the_type.get_type_id();
  }
}

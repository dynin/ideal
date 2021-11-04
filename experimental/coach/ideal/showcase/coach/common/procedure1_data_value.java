/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.showcase.coach.reflections.*;
public class procedure1_data_value extends procedure1<composite_data_value> {
  public procedure1_data_value(String s) {
    super(s, composite_data_value.class);
  }

  @Override
  protected string to_string(composite_data_value the_value) {
    return the_value.get_data_id();
  }
}

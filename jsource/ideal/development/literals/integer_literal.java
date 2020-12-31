/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class integer_literal extends debuggable implements literal<Integer> {

  private final int the_value;
  private final string image;

  public integer_literal(int the_value, string image) {
    this.the_value = the_value;
    this.image = image;
  }

  public integer_literal(int the_value) {
    this(the_value, new base_string(String.valueOf(the_value)));
  }

  @Override
  public Integer the_value() {
    return the_value;
  }

  @Override
  public string to_string() {
    return image;
  }
}

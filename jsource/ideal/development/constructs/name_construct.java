/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class name_construct extends base_construct implements stringable {
  public action_name the_name;
  public name_construct(action_name the_name, origin pos) {
    super(pos);
    this.the_name = the_name;
  }

  public readonly_list<construct> children() {
    return new empty<construct>();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_name);
  }
}

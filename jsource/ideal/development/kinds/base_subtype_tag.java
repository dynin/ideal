/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.kinds;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class base_subtype_tag extends debuggable implements subtype_tag,
    readonly_displayable {

  private final simple_name name;

  public base_subtype_tag(String name) {
    this.name = simple_name.make(name);
  }

  public simple_name name() {
    return name;
  }

  public string to_string() {
    return name_utilities.in_brackets(name);
  }

  public string display() {
    return to_string();
  }
}

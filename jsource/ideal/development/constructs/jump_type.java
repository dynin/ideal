/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public enum jump_type implements deeply_immutable_data, stringable {
  BREAK_JUMP("break"),
  CONTINUE_JUMP("continue");
  // TODO: GOTO_JUMP etc...

  private final base_string name;

  private jump_type(String name) {
    this.name = new base_string(name);
  }

  public simple_name jump_name() {
    return simple_name.make(name);
  }

  @Override
  public string to_string() {
    return name;
  }

  @Override
  public String toString() {
    return name.s();
  }
}

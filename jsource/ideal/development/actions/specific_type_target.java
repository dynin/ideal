/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
public class specific_type_target extends debuggable implements action_target {
  public final type target_type;

  public specific_type_target(type target_type) {
    this.target_type = target_type;
  }

  @Override
  public boolean matches(abstract_value the_abstract_value) {
    return the_abstract_value.type_bound() == target_type;
  }

  @Override
  public string to_string() {
    return new base_string("target: ", target_type.to_string());
  }
}

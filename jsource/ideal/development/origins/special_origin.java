/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.origins;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;

public class special_origin extends debuggable implements origin, deeply_immutable_data,
    stringable {

  public final string description;

  public special_origin(string description) {
    this.description = description;
  }

  @Override
  public @Nullable origin deeper_origin() {
    return null;
  }

  @Override
  public string to_string() {
    return description;
  }
}

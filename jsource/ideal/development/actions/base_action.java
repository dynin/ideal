/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;

public abstract class base_action extends debuggable implements action {
  private final origin the_origin;

  public base_action(origin the_origin) {
    assert the_origin != null;
    this.the_origin = the_origin;
  }

  @Override
  public final origin deeper_origin() {
    return the_origin;
  }

  @Override
  public action to_action() {
    return this;
  }

  @Override
  public action combine(action from, origin the_origin) {
    return this;
  }

  // TODO: subtypes may override this.
  @Override
  public @Nullable declaration get_declaration() {
    return null;
  }
}

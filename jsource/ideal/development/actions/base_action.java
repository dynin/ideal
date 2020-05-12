/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;

public abstract class base_action<S extends origin> extends debuggable implements action {
  public final S source;

  public base_action(S source) {
    assert source != null;
    this.source = source;
  }

  @Override
  public final S deeper_origin() {
    return source;
  }

  // TODO: subtypes may override this.
  @Override
  public @Nullable declaration get_declaration() {
    return null;
  }

  @Override
  public action bind_from(action from, origin pos) {
    // TODO: subtypes should override this to update the source origin.
    return this;
  }
}

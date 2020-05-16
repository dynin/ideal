/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.components.*;

public abstract class extension_construct extends base_construct {
  public extension_construct(origin source) {
    super(source);
  }

  public abstract readonly_list<construct> children();

  public abstract analyzable to_analyzable();

  public abstract text_fragment print(printer p);

  public abstract boolean is_terminated();

  public abstract construct transform(transformer t);
}

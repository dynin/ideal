/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.resources;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;
import ideal.runtime.resources.*;

public class filesystem {

  // We allow using ".." here; we might want to change that.
  public static final resource_catalog CURRENT_CATALOG =
      new file_store(resource_util.CURRENT_CATALOG, true).top();
  public static final resource_catalog ROOT =
      new file_store(resource_util.ROOT_CATALOG, false).top();
}

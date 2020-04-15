/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.development.elements.*;

import javax.annotation.Nullable;

public interface type_announcement extends type_declaration {
  type_declaration get_type_declaration();
}

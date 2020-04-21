/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.elements;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

public interface token_type extends identifier, reference_equality {
  string name();
  // TODO: this can be replaced by checking for the keyword type.
  boolean is_keyword();
  // TODO: we can retire this once we stop using javacup. 
  int symbol();
}

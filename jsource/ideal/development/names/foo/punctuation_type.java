/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

public class punctuation_type extends base_token_type {
  public punctuation_type(String name, int base_symbol) {
    super(name, base_symbol);
  }

  public punctuation_type(String name) {
    super(name);
  }
}

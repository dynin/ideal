/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.runtime.elements.*;
import ideal.development.origins.*;

public abstract class base_scanner_element implements scanner_element {
  protected scanner_config config;

  @Override
  public void set_config(scanner_config the_scanner_config) {
    config = the_scanner_config;
  }

  protected character_handler the_character_handler() {
    return config.the_character_handler();
  }
}

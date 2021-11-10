/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class string_fragment extends literal_fragment {
  public final string the_string;

  public string_fragment(string the_string) {
    this.the_string = the_string;
  }

  @Override
  public string to_string() {
    return the_string;
  }
}

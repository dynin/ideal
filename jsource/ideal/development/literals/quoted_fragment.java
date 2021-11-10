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

public class quoted_fragment extends literal_fragment {
  public final quoted_character the_quoted_character;

  public quoted_fragment(quoted_character the_quoted_character) {
    this.the_quoted_character = the_quoted_character;
  }

  @Override
  public string to_string() {
    return the_quoted_character.with_escape();
  }
}

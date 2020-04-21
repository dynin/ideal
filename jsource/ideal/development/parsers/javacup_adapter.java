/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.parsers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

public class javacup_adapter implements Scanner {
  private readonly_list<token> tokens;
  private int index;

  public javacup_adapter(readonly_list<token> tokens) {
    this.tokens = tokens;
    this.index = 0;
  }

  /** Return the next token, or <code>null</code> on end-of-file. */
  public Symbol next_token() {
    if (index < tokens.size()) {
      token t = tokens.get(index);
      index += 1;
      return new Symbol(t.type().symbol(), t);
    } else {
      return null;
    }
  }
}

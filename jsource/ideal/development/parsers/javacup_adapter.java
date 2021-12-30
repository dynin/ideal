/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.parsers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.symbols.*;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

public class javacup_adapter implements Scanner {
  private static dictionary<string, Integer> symbols_map = base_symbols_map.symbols_map;

  private readonly_list<token> tokens;
  private int index;

  public javacup_adapter(readonly_list<token> tokens) {
    this.tokens = tokens;
    this.index = 0;
  }

  /** Return the next token, or <code>null</code> on end-of-file. */
  public Symbol next_token() {
    if (index < tokens.size()) {
      token the_token = tokens.get(index);
      index += 1;
      Integer symbol_id = symbols_map.get(the_token.type().symbol_identifier());
      assert symbol_id != null;
      return new Symbol(symbol_id, the_token);
    } else {
      return null;
    }
  }
}

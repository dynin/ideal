/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.parsers;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;

import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.scanners.*;
import ideal.development.constructs.*;

import java_cup.runtime.Symbol;

public class loader {

  public static list<construct> parse(readonly_list<token> tokens) {
    javacup_adapter scanner = new javacup_adapter(tokens);
    base_wrapper parser = new base_wrapper(scanner);

    /* open input files, etc. here */
    Symbol parse_tree = null;

    boolean do_debug_parse = false;

    try {
      if (do_debug_parse)
        parse_tree = parser.debug_parse();
      else
        parse_tree = parser.parse();
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
    } finally {
      /* do close out here */
    }

    if (parse_tree == null) {
      utilities.panic("Parse tree is null");
      return null;
    } else {
      return (list<construct>) parse_tree.value;
    }
  }
}

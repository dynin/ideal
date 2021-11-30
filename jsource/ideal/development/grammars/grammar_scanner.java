/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.grammars;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;
import ideal.development.comments.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.names.*;
import ideal.development.analyzers.*;
import ideal.development.extensions.*;

public class grammar_scanner {
  public void register_tokens(scanner_config the_config) {
    the_config.add_punctuation(punctuation.COLON_COLON_EQUALS);

    the_config.add_keyword(keywords.GRAMMAR);
    the_config.add_keyword(keywords.TERMINAL);
    the_config.add_keyword(keywords.NONTERMINAL);
  }
}

/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.development.elements.*;
import generated.ideal.development.parsers.base_symbols;

public interface special_token_type {

  token_type SIMPLE_NAME = new base_token_type("<simple-name>", base_symbols.SIMPLE_NAME);
  token_type SPECIAL_NAME = new base_token_type("<special-name>", base_symbols.SPECIAL_NAME);

  token_type KIND = new base_token_type("<kind>", base_symbols.KIND);
  token_type MODIFIER_KIND = new base_token_type("<modifier-kind>", base_symbols.MODIFIER_KIND);
  token_type SUPERTYPE_KIND = new base_token_type("<supertype-kind>", base_symbols.SUPERTYPE_KIND);
  token_type FLAVOR = new base_token_type("<flavor>", base_symbols.FLAVOR);
  token_type JUMP = new base_token_type("<jump>", base_symbols.JUMP);

  token_type LITERAL = new base_token_type("<literal>", base_symbols.LITERAL);

  token_type COMMENT = new base_token_type("<comment>", base_symbols.COMMENT);

  //token_type TEMPLATE = new base_token_type("<template>", base_symbols.TEMPLATE);
}

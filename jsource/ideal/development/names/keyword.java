/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.development.elements.*;
import ideal.development.symbols.base_symbols;

public interface keyword extends token_type {

  simple_name FOR_NAME = simple_name.make("for");

  token_type THIS = new base_token_type("this");
  token_type SUPER = new base_token_type("super");
  token_type NEW = new base_token_type("new");

  token_type OR = new base_token_type("or", base_symbols.OR);

  token_type AS = new base_token_type("as", base_symbols.AS);
  token_type IS = new base_token_type("is", base_symbols.IS);
  token_type IS_NOT = new base_token_type("is_not", base_symbols.IS_NOT);

  token_type ASSERT = new base_token_type("assert", base_symbols.ASSERT);

  token_type RETURN = new base_token_type("return", base_symbols.RETURN);
  token_type IF = new base_token_type("if", base_symbols.IF);
  token_type ELSE = new base_token_type("else", base_symbols.ELSE);

  token_type LOOP = new base_token_type("loop", base_symbols.LOOP);

  token_type WHILE = new base_token_type("while", base_symbols.WHILE);
  token_type FOR = new base_token_type("for", base_symbols.FOR);
  token_type IMPORT = new base_token_type("import", base_symbols.IMPORT);
  token_type USE = new base_token_type("use", base_symbols.USE);
  token_type TARGET = new base_token_type("target", base_symbols.TARGET);

  token_type PLEASE = new base_token_type("please", base_symbols.PLEASE);
}

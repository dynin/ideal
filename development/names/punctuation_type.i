-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class punctuation_type {
  extends base_token_type;

  punctuation_type(string name, string the_symbol_identifier) {
    super(name, the_symbol_identifier);
  }
}

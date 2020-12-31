-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class punctuation_type {
  extends base_token_type;

  overload punctuation_type(string name, integer base_symbol) {
    super(name, base_symbol);
  }

  overload punctuation_type(string name) {
    super(name);
  }
}

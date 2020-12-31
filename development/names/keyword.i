-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Keywords used in the ideal system.
class keyword {
  extends base_token_type;

  overload keyword(string name, integer the_symbol) {
    super(name, the_symbol);
  }

  overload keyword(string name) {
    super(name);
  }

  simple_name keyword_name => simple_name.make(name);
}

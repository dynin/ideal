-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Keywords used in the ideal system.
class keyword {
  extends base_token_type;

  keyword(string name) {
    super(name, unicode_handler.instance.to_upper_case_all(name));
  }

  simple_name keyword_name => simple_name.make(name);
}

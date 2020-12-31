-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- References a text fragment, given it's beginning, main part, and end.
--- All three |origin|s should be instances of |text_origin|.
class fragment_origin {
  extends debuggable;
  implements deeply_immutable data, mutable origin;

  origin begin;
  origin main;
  origin end;

  fragment_origin(origin begin, origin main, origin end) {
    this.begin = begin;
    this.main = main;
    this.end = end;
  }

  implement origin deeper_origin => main;
}

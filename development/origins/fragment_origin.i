-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

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

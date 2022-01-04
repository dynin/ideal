-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class string_fragment {
  extends literal_fragment;

  the string;

  string_fragment(the string) {
    this.the_string = the_string;
  }

  override string to_string => the_string;
}

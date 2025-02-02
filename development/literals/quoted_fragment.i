-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class quoted_fragment {
  extends literal_fragment;

  the quoted_character;

  quoted_fragment(the quoted_character) {
    this.the_quoted_character = the_quoted_character;
  }

  override string to_string => the_quoted_character.with_escape;
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class quoted_fragment {
  extends literal_fragment;

  the quoted_character;

  quoted_fragment(the quoted_character) {
    this.the_quoted_character = the_quoted_character;
  }

  override string to_string => the_quoted_character.with_escape;
}

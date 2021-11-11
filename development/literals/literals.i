-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Integer and string literals.
package literals {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.characters;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;
  import ideal.machine.channels.string_writer;

  class literal_fragment;
  class string_fragment;
  class quoted_fragment;
  --class string_literal;
  --class integer_literal;
}

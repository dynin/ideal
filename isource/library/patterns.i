-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

package patterns {
  implicit import ideal.library.elements;

  --- A pattern is a predicate on lists.
  interface pattern[contravariant readonly value element_type] {
    extends predicate[readonly list[element_type]];

    boolean is_viable_prefix(readonly list[element_type] the_list);
    range or null find_in(readonly list[element_type] the_list, nonnegative start_index);

    immutable list[immutable list[element_type]] split(immutable list[element_type] the_list);
  }
}

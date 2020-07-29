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
    -- TODO: default null for start_index
    range or null find_first(readonly list[element_type] the_list, nonnegative start_index);

    immutable list[immutable list[element_type]] split(immutable list[element_type] the_list);
  }

  interface reversible_pattern[contravariant readonly value element_type] {
    extends pattern[element_type];

    range or null find_last(readonly list[element_type] the_list, nonnegative or null end_index);
  }
}

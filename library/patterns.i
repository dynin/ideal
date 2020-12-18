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

    --- Check whether the given list can be a start of the sequence that matches this pattern.
    boolean is_viable_prefix(readonly list[element_type] the_list);

    --- Returns the maximum number of the elements of a given list that matches the pattern,
    --- or |null| if there is no prefix match.
    nonnegative or null match_prefix(readonly list[element_type] the_list);

    --- Gets the first non-empty match for this pattern.
    -- TODO: default null for start_index
    range or null find_first(readonly list[element_type] the_list, nonnegative start_index);

    immutable list[immutable list[element_type]] split(immutable list[element_type] the_list);
  }

  --- A reversible_pattern is a pattern that can be matched end-to-start.
  interface reversible_pattern[contravariant readonly value element_type] {
    extends pattern[element_type];

    --- Gets the last non-empty match in the end-to-start direction.
    --- When |end_index| is specified, it defines the element index after the last element
    --- index after the one being matched.  If |end_index| is null, it defaults to
    --- |the_list.size|.
    -- TODO: default null for end_index
    range or null find_last(readonly list[element_type] the_list, nonnegative or null end_index);
  }
}

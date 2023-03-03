-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declarations for patterns: a pattern is a predicate on lists with
--- extra operations.
package patterns {
  implicit import ideal.library.elements;

  --- A pattern is a predicate on lists.
  interface pattern[contravariant readonly value element_type] {
    extends predicate[readonly list[element_type]];

    --- Check whether the given list can be a start of the sequence that matches this pattern.
    boolean is_viable_prefix(the readonly list[element_type]);

    --- Returns the maximum number of the elements of a given list that matches the pattern,
    --- or |null| if there is no prefix match.
    --- This is a greedy match: it matches the longest prefix.
    nonnegative or null match_prefix(the readonly list[element_type]);

    --- Gets the first non-empty match for this pattern.
    -- TODO: default null for start_index
    range or null find_first(the readonly list[element_type], nonnegative start_index);

    immutable list[immutable list[element_type]] split(the immutable list[element_type]);
  }

  --- A reversible_pattern is a pattern that can be matched end-to-start.
  interface reversible_pattern[contravariant readonly value element_type] {
    extends pattern[element_type];

    --- Gets the last non-empty match in the end-to-start direction.
    --- When |end_index| is specified, it defines the element index after the last element
    --- index after the one being matched.  If |end_index| is null, it defaults to
    --- the size of the list.
    -- TODO: default null for end_index
    range or null find_last(the readonly list[element_type], nonnegative or null end_index);
  }

  --- A matcher is a pattern with an associated parse procedure that generates a result.
  interface matcher[contravariant readonly value element_type, covariant any value result_type] {
    extends pattern[element_type];

    --- Parse the list and produce a result.
    --- Assumes this pattern matches the list.
    result_type parse(the readonly list[element_type]);
  }
}

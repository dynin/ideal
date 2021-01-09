-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Matcher for a repeated pattern.
class repeat_matcher[readonly value element_type, covariant any value result_type,
    any value intermediate_type] {
  extends repeat_pattern[element_type];
  implements matcher[element_type, result_type];

  procedure[result_type, readonly list[intermediate_type]] list_converter;

  repeat_matcher(matcher[element_type, intermediate_type] the_pattern, boolean do_match_empty,
      procedure[result_type, readonly list[intermediate_type]] list_converter) {
    super(the_pattern, do_match_empty);
    this.list_converter = list_converter;
  }

  implement result_type parse(readonly list[element_type] the_list) {
    var nonnegative index : 0;
    intermediate_list : base_list[intermediate_type].new();

    while (index < the_list.size) {
      match : the_pattern.match_prefix(the_list.skip(index));
      if (match is null) {
        utilities.panic("Can't parse list in repeat_matcher");
      }
      assert match > 0;
      matched : (the_pattern as matcher[element_type, intermediate_type]).parse(
          the_list.slice(index, index + match));
      intermediate_list.append(matched);
      index += match;
    }

    return list_converter(intermediate_list);
  }
}

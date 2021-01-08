-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Matcher for a repeated pattern.
abstract class repeat_matcher[readonly value element_type, covariant any value result_type] {
  extends repeat_pattern[element_type];
  implements matcher[element_type, result_type];

  repeat_matcher(matcher[element_type, result_type] the_pattern, boolean do_match_empty) {
    super(the_pattern, do_match_empty);
  }

  implement result_type or null parse(readonly list[element_type] the_list) {
    var nonnegative index : 0;
    var result_type or null result : missing.instance;

    while (index < the_list.size) {
      match : the_pattern.match_prefix(the_list.skip(index));
      if (match is null) {
        utilities.panic("Can't parse list in repeat_matcher");
      }
      assert match > 0;
      matched : (the_pattern as matcher[element_type, result_type]).parse(
          the_list.slice(index, index + match));
      if (result is null) {
        result = matched;
      } else if (matched is_not null) {
        result = combine(result, matched);
      }
      index += match;
    }
    return result;
  }

  protected abstract result_type combine(result_type first, result_type second);
}

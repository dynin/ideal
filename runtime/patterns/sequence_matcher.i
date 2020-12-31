-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Matcher for a sequence of patterns.
class sequence_matcher[readonly value element_type, covariant any value result_type] {
  extends sequence_pattern[element_type];
  implements matcher[element_type, result_type];

  procedure[result_type, readonly list[any value]] matcher_procedure;

  sequence_matcher(readonly list[pattern[element_type]] patterns_list,
      procedure[result_type, readonly list[any value]] matcher_procedure) {
    super(patterns_list);
    this.matcher_procedure = matcher_procedure;
  }

  implement result_type parse(readonly list[element_type] the_list) {
    matches : base_list[any value].new();
    var nonnegative index : 0;
    var nonnegative prefix : 0;

    while (index < patterns_list.size) {
      pattern_element : patterns_list[index];
      match : pattern_element.match_prefix(the_list.skip(prefix));
      assert match is_not null;
      if (pattern_element is matcher) {
        matcher_element : pattern_element as matcher[element_type, any value];
        matches.append(matcher_element.parse(the_list.slice(prefix, prefix + match)));
      } else {
        matches.append(missing.instance);
      }
      prefix += match;
      index += 1;
    }

    assert prefix == the_list.size;
    return matcher_procedure(matches);
  }
}

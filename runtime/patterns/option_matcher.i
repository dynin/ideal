-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Matcher for one of a collection of patterns.
class option_matcher[readonly value element_type, covariant any value result_type] {
  extends option_pattern[element_type];
  implements matcher[element_type, result_type];

  option_matcher(readonly collection[matcher[element_type, result_type]] matchers) {
    -- TODO: the cast should be redundant
    super(matchers !> readonly collection[pattern[element_type]]);
  }

  void add_matcher(matcher[element_type, result_type] the_matcher) {
    add_option(the_matcher);
  }

  implement result_type or null parse(readonly list[element_type] the_list) {
    for (option : options) {
      if (option(the_list)) {
        if (option is matcher) {
          the_matcher : option !> matcher[element_type, any value];
          return the_matcher.parse(the_list) !> result_type;
        } else {
          return missing.instance;
        }
      }
    }

    utilities.panic("Can't parse list in option_matcher");
  }
}

-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class punctuation_element {
  extends base_scanner_element;

  protected punctuation_type the_punctuation;
  private pattern[character] punctuation_pattern;

  punctuation_element(punctuation_type the_punctuation) {
    this.the_punctuation = the_punctuation;
    punctuation_pattern = list_pattern[character].new(the_punctuation.name);
  }

  override scan_state or null process(source_content source, nonnegative begin) {
    input : source.content;
    match : punctuation_pattern.match_prefix(input.skip(begin));
    if (match is null) {
      return missing.instance;
    }

    end : begin + match;
    the origin : source.make_origin(begin, end);
    image : input.slice(begin, end);
    return scan_state.new(base_token[string].new(the_punctuation, image, the_origin), end, end);
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class string_token_element[deeply_immutable data payload_type] {
  extends base_scanner_element;

  protected punctuation_type the_punctuation;
  protected token_type the_type;
  protected payload_type the_payload;
  protected pattern[character] punctuation_pattern;

  string_token_element(punctuation_type the_punctuation, token_type the_type,
      payload_type the_payload) {
    this.the_punctuation = the_punctuation;
    this.the_type = the_type;
    this.the_payload = the_payload;
    this.punctuation_pattern = list_pattern[character].new(the_punctuation.name);
  }

  override scan_state or null process(source_content source, nonnegative begin) {
    input : source.content;
    match : punctuation_pattern.match_prefix(input.skip(begin));
    if (match is null) {
      return missing.instance;
    }
    end : begin + match;
    the_origin : source.make_origin(begin, end);
    return scan_state.new(
        base_token[payload_type].new(the_type, the_payload, the_origin), end, end);
  }
}

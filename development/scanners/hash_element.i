-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class hash_element {
  extends base_scanner_element;

  private punctuation_type the_token_type;
  private character hash_character;

  private static ID_PATTERN : list_pattern[character].new("id:");

  hash_element(punctuation_type the_token_type) {
    this.the_token_type = the_token_type;
    name : the_token_type.name;
    assert name.size == 1;
    hash_character = name.first;
  }

  override scan_state or null process(source_content source, nonnegative begin) {
    input : source.content;
    if (input[begin] != hash_character) {
      return missing.instance;
    }

    end : begin + 1;
    match : ID_PATTERN.match_prefix(input.skip(end));

    if (match is_not null) {
      identifier_begin : end + match;
      if (identifier_begin < input.size && config.is_name_start(input[identifier_begin])) {
        var identifier_end : identifier_begin + 1;
        for (; identifier_end < input.size; identifier_end += 1) {
          if (!config.is_name_part(input[identifier_end])) {
            break;
          }
        }
        the origin : source.make_origin(begin, identifier_end);
        token_as_name : simple_name.make(source.content.slice(identifier_begin, identifier_end));
        return scan_state.new(base_token[simple_name].new(special_token_type.SIMPLE_NAME,
            token_as_name, the_origin), identifier_end, identifier_end);
      }
    }

    the origin : source.make_origin(begin, end);
    image : input.slice(begin, end);
    return scan_state.new(base_token[string].new(the_token_type, image, the_origin), end, end);
  }
}

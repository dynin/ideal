-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class line_comment {
  extends base_scanner_element;

  private the comment_type;
  private scanner_element start;

  line_comment(punctuation_type start_punctuation, the comment_type) {
    this.start = punctuation_element.new(start_punctuation);
    this.the_comment_type = the_comment_type;
  }

  override scan_state or null process(source_content source, nonnegative begin) {
    result : start.process(source, begin);
    if (result is null) {
      return missing.instance;
    }

    input : source.content;
    var nonnegative end;
    for (end = result.end; end < input.size; end += 1) {
      if (input[end] == '\n') {
        end += 1;
        break;
      }
    }

    image : input.slice(begin, end);
    content : input.slice(result.end, end);
    the origin : source.make_origin(begin, end);
    comment : base_token[comment].new(special_token_type.COMMENT,
        comment.new(the_comment_type, content, image), the_origin);
    return scan_state.new(comment, result.end, end);
  }
}

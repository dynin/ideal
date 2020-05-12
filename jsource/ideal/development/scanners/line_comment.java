/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.notifications.*;

public class line_comment implements scanner_element {
  private comment_type type;
  private final scanner_element start;

  public line_comment(punctuation_type start_punctuation, comment_type type) {
    this.start = new punctuation_element(start_punctuation);
    this.type = type;
  }

  @Override
  public scan_state process(source_content source, int begin) {
    scan_state result = start.process(source, begin);
    if (result == null) {
      return null;
    }
    string input = source.content;
    int end;
    for (end = result.end; end < input.size(); ++end) {
      if (input.get(end) == '\n') {
        end += 1;
        break;
      }
    }
    string image = input.slice(begin, end);
    string content = input.slice(result.end, end);
    origin pos = source.make_origin(begin, end);
    token comment = new base_token<comment>(special_token_type.COMMENT,
        new comment(type, content, image), pos);
    return new scan_state(comment, result.end, end);
  }
}

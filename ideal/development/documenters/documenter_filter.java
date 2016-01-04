/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.documenters;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.machine.channels.string_writer;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.scanners.*;

public class documenter_filter {

  private documenter_filter() { }

  public static readonly_list<token> transform(readonly_list<token> tokens) {
    list<token> result = new base_list<token>();
    int index = 0;
    while (index < tokens.size()) {
      token next_token = tokens.get(index);
      if (next_token.type() == special_token_type.COMMENT) {
        token<comment> the_comment = (token<comment>) next_token;
        if (the_comment.payload().type.is_doc) {
          index = handle_doc_comment(tokens, index, result, the_comment);
        } else {
          ++index;
        }
      } else {
        result.append(next_token);
        ++index;
      }
    }
    return result;
  }

  private static int handle_doc_comment(readonly_list<token> tokens, int index, list<token> result,
      position pos) {
    string_writer content = new string_writer();

    while (index < tokens.size()) {
      token next_token = tokens.get(index);
      if (next_token.type() != special_token_type.COMMENT) {
        break;
      }

      token<comment> the_comment = (token<comment>) next_token;
      if (the_comment.payload().type.is_doc) {
        content.write_all(the_comment.payload().content);
        if (pos == null) {
          pos = the_comment;
        }
        ++index;
      } else if (the_comment.payload().type == comment_type.WHITESPACE) {
        ++index;
      } else {
        break;
      }
    }

    string doc = content.elements();
    result.append(new base_token<comment>(special_token_type.COMMENT,
        new comment(comment_type.BLOCK_DOC_COMMENT, doc, doc), pos));

    return index;
  }
}

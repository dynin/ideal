-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

namespace documenter_filter {

  readonly list[token[deeply_immutable data]] transform(
      readonly list[token[deeply_immutable data]] tokens) {

    result : base_list[token[deeply_immutable data]].new();
    var nonnegative index : 0;
    while (index < tokens.size) {
      next_token : tokens[index];
      if (next_token.type == special_token_type.COMMENT) {
        the_comment : next_token !> token[comment];
        if (the_comment.payload.type.is_doc) {
          index = handle_doc_comment(tokens, index, result, the_comment);
        } else {
          index += 1;
        }
      } else {
        if (next_token.type == keywords.RESERVED) {
          base_notification.new(messages.reserved_word, next_token).report();
          the_keyword : (next_token !> token[keyword]).payload;
          if (the_keyword != keywords.RESERVED) {
            result.append(base_token[keyword].new(the_keyword, the_keyword, next_token));
          }
        } else {
          result.append(next_token);
        }
        index += 1;
      }
    }
    return result;
  }

  private nonnegative handle_doc_comment(readonly list[token[deeply_immutable data]] tokens,
      var nonnegative index, list[token[deeply_immutable data]] result, var the origin) {

    content : string_writer.new();

    while (index < tokens.size) {
      next_token : tokens[index];
      if (next_token.type != special_token_type.COMMENT) {
        break;
      }

      the_comment : next_token !> token[comment];
      if (the_comment.payload.type.is_doc) {
        content.write_all(the_comment.payload.content);
        if (the_origin is null) {
          the_origin = the_comment;
        }
        index += 1;
      } else if (the_comment.payload.type == comment_type.WHITESPACE) {
        index += 1;
      } else {
        break;
      }
    }

    doc_content : content.elements;
    result.append(base_token[comment].new(special_token_type.COMMENT,
        comment.new(comment_type.BLOCK_DOC_COMMENT, doc_content, doc_content), the_origin));

    return index;
  }
}

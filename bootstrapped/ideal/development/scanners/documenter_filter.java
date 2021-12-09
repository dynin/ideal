// Autogenerated from development/scanners/documenter_filter.i

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.runtime.patterns.*;
import ideal.runtime.logs.*;
import ideal.machine.characters.*;
import ideal.machine.channels.string_writer;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.notifications.*;
import ideal.development.origins.*;
import ideal.development.comments.*;
import ideal.development.literals.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.constraint_category;
import ideal.development.jumps.jump_category;

public class documenter_filter {
  public static readonly_list<token<Object>> transform(final readonly_list<token<Object>> tokens) {
    final base_list<token<Object>> result = new base_list<token<Object>>();
    Integer index = 0;
    while (index < tokens.size()) {
      final token<Object> next_token = tokens.get(index);
      if (next_token.type() == special_token_type.COMMENT) {
        final token<comment> the_comment = (token<comment>) (Object) next_token;
        if (the_comment.payload().type.is_doc) {
          index = documenter_filter.handle_doc_comment(tokens, index, result, the_comment);
        } else {
          index += 1;
        }
      } else {
        if (next_token.type() == keywords.RESERVED) {
          new base_notification(messages.reserved_word, next_token).report();
          final keyword the_keyword = ((token<keyword>) (Object) next_token).payload();
          if (the_keyword != keywords.RESERVED) {
            result.append(((token<Object>) (Object) new base_token<keyword>(the_keyword, the_keyword, next_token)));
          }
        } else {
          result.append(next_token);
        }
        index += 1;
      }
    }
    return result;
  }
  private static Integer handle_doc_comment(final readonly_list<token<Object>> tokens, Integer index, final list<token<Object>> result, origin the_origin) {
    final string_writer content = new string_writer();
    while (index < tokens.size()) {
      final token<Object> next_token = tokens.get(index);
      if (next_token.type() != special_token_type.COMMENT) {
        break;
      }
      final token<comment> the_comment = (token<comment>) (Object) next_token;
      if (the_comment.payload().type.is_doc) {
        content.write_all(the_comment.payload().content);
        if (the_origin == null) {
          the_origin = the_comment;
        }
        index += 1;
      } else if (the_comment.payload().type == comment_type.WHITESPACE) {
        index += 1;
      } else {
        break;
      }
    }
    final string doc_content = content.elements();
    result.append(((token<Object>) (Object) new base_token<comment>(special_token_type.COMMENT, new comment(comment_type.BLOCK_DOC_COMMENT, doc_content, doc_content), the_origin)));
    return index;
  }
}
-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class scanner_engine {
  private scanner_config config;

  scanner_engine(scanner_config config) {
    this.config = config;
  }

  readonly list[token[deeply_immutable data]] scan(source_content source) {
    input : source.content;
    tokens : base_list[token[deeply_immutable data]].new();
    for (var nonnegative begin : 0; begin < input.size; ) {
      the_character : input[begin];

      if (config.is_whitespace(the_character)) {
        var end : begin + 1;
        for (; end < input.size; end += 1) {
          if (!config.is_whitespace(input[end])) {
            break;
          }
        }
        the origin : source.make_origin(begin, end);
        image : input.slice(begin, end);
        -- TODO: handle newlines specially.
        the_token : base_token[comment].new(special_token_type.COMMENT,
            comment.new(comment_type.WHITESPACE, image, image), the_origin);
        -- TODO : the cast is redundant!
        tokens.append(the_token !> token[deeply_immutable data]);
        begin = end;
      } else if (config.is_name_start(the_character)) {
        var end : begin + 1;
        for (; end < input.size; end += 1) {
          if (!config.is_name_part(input[end])) {
            break;
          }
        }
        the origin : source.make_origin(begin, end);
        image : input.slice(begin, end);
        token_as_name : simple_name.make(image);
        the_token : base_token[simple_name].new(special_token_type.SIMPLE_NAME,
            token_as_name, the_origin);
        -- TODO : the cast is redundant!
        tokens.append(config.process_token(the_token !> token[deeply_immutable data]));
        begin = end;
      } else {
        var scan_state or null next : missing.instance;
        for (element : config.elements) {
          processed : element.process(source, begin);
          if (processed is_not null) {
            if (next is null) {
              next = processed;
            } else {
              compare : processed.compare_to(next);
              assert compare != 0;
              if (compare > 0) {
                next = processed;
              }
            }
          }
        }

        if (next is_not null) {
          -- TODO : the cast is redundant!
          tokens.append(next.token !> token[deeply_immutable data]);
          begin = next.end;
          continue;
        }

        end : begin + 1;
        the origin : source.make_origin(begin, end);
        base_notification.new(messages.unrecognized_character, the_origin).report();
        begin = end;
      }
    }

    return tokens;
  }
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.

-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.library.characters;
import ideal.machine.channels.string_writer;

namespace name_utilities {

  private the_character_handler : unicode_handler.instance;

  string in_brackets(readonly stringable name) pure {
    return "<" ++ name.to_string ++ ">";
  }

  simple_name join(simple_name first, simple_name second) pure {
    -- TODO: use list.join() once it's implemented
    segments : base_list[string].new();

    segments.append_all(first.segments);
    segments.append_all(second.segments);

    return simple_name.make_from_segments(segments.frozen_copy);
  }

  -- TODO: convert into a pattern matcher
  simple_name parse_camel_case(string name) {
    segments : base_list[string].new();
    var nonnegative index : 0;

    while (index < name.size) {
      the_writer : string_writer.new();

      while (index < name.size && the_character_handler.is_upper_case(name[index])) {
        the_writer.write(the_character_handler.to_lower_case(name[index]));
        index += 1;
      }

      while (index < name.size && !the_character_handler.is_upper_case(name[index])) {
        the_writer.write(name[index]);
        index += 1;
      }

      segments.append(the_writer.elements);
    }

    if (segments.is_not_empty) {
      return simple_name.make_from_segments(segments.frozen_copy);
    } else {
      utilities.panic("Can't parse name " ++ name);
    }
  }
}

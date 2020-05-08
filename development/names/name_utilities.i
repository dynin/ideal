-- Copyright 2014-2020 The Ideal Authors. All rights reserved.

-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.characters;
implicit import ideal.machine.characters;
import ideal.machine.channels.string_writer;

namespace name_utilities {

  private FIRST : simple_name.make("first");
  private SECOND : simple_name.make("second");
  private THIRD : simple_name.make("third");

  private the_character_handler : normal_handler.instance;

  string in_brackets(readonly stringable name) pure {
    return "<" ++ name.to_string ++ ">";
  }

  simple_name make_numbered_name(nonnegative index) pure {
    if (index == 0) {
      return FIRST;
    } else if (index == 1) {
      return SECOND;
    } else if (index == 2) {
      return THIRD;
    } else {
      utilities.panic("Don't know how to count up to " ++ index);
    }
  }
-- TODO: implement switch
--    switch (index) {
--      case 0:
--        return FIRST;
--      case 1:
--        return SECOND;
--      case 2:
--        return THIRD;
--      default:
--        utilities.panic("Don't know how to count up to " + index);
--        return null;
--    }
--  }

  simple_name join(simple_name first, simple_name second) pure {
    segments : base_list[string].new();

    segments.append_all(first.segments);
    segments.append_all(second.segments);

    return simple_name.make_from_segments(segments.frozen_copy());
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

      segments.append(the_writer.extract_elements());
    }

    if (!segments.is_empty) {
      return simple_name.make_from_segments(segments.frozen_copy());
    } else {
      utilities.panic("Can't parse name " ++ name);
    }
  }
}

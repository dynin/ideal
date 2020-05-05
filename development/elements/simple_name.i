-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.runtime.elements;
import ideal.runtime.logs.displayable;
import ideal.machine.channels.string_writer;
--import ideal.runtime.patterns.singleton_pattern;
import ideal.machine.adapters.java.lang.String;

--- Identifiers used in the ideal system.
--- These are multiple segments separated by underscore.
class simple_name {
  extends debuggable;
  implements action_name, displayable;

  the_underscore : '_';
  --the_pattern : singleton_pattern[character].new(the_underscore);

  private static final dictionary[immutable list[string], simple_name] all_names :
    hash_dictionary[immutable list[string], simple_name].new();

  public final immutable list[string] segments;

  private simple_name(immutable list[string] segments) {
    this.segments = segments;
  }

  override string to_string() {
    if (segments.size == 1) {
      return segments.first;
    } else {
      -- TODO: use list[string].join();
      the_writer : string_writer.new();
      for (var nonnegative i : 0; i < segments.size; i += 1) {
        the_writer.write_all(segments[i]);
        if (i != segments.size - 1) {
          the_writer.write(the_underscore);
        }
      }
      return the_writer.extract_elements();
    }
  }

  override string display() {
    return to_string();
  }

  public static simple_name make_from_segments(immutable list[string] segments) {
    assert !segments.is_empty;
    existing_name : all_names.get(segments); -- TODO: use []

    if (existing_name is_not null) {
      return existing_name;
    }

    result : simple_name.new(segments);
    all_names.put(segments, result);
    return result;
  }

  public overload static simple_name make(String name) {
    assert name.length() > 0;

    -- TODO: use pattern.split()
    list[string] segments : base_list[string].new();
    var integer index : 0;

    while (true) {
      -- TODO: drop indexOf from adapters.
      underscore : name.indexOf('_', index);
      if (underscore < 0) {
        segments.append(base_string.new(name.substring(index)));
        break;
      }
      segments.append(base_string.new(name.substring(index, underscore)));
      index = underscore + 1;
    }

    return make_from_segments(segments.frozen_copy());
  }

  public overload static simple_name make(string name) {
    return make(utilities.s(name));
  }
}

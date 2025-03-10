-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.runtime.elements;
import ideal.runtime.logs.displayable;
import ideal.machine.channels.string_writer;
import ideal.runtime.patterns.singleton_pattern;
import ideal.machine.adapters.java.lang.String;

--- Identifiers used in the ideal system.
--- These are multiple segments separated by underscore.
class simple_name {
  extends debuggable;
  implements action_name, displayable;

  static the_underscore : '_';
  static the_pattern : singleton_pattern[character].new(the_underscore);

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
      -- TODO: use range operator
      for (var nonnegative i : 0; i < segments.size; i += 1) {
        the_writer.write_all(segments[i]);
        if (i != segments.size - 1) {
          the_writer.write(the_underscore);
        }
      }
      return the_writer.elements;
    }
  }

  override string display() {
    return to_string();
  }

  public static simple_name make_from_segments(immutable list[string] segments) {
    assert segments.is_not_empty;
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

    segments_list : the_pattern.split(base_string.new(name));
    -- TODO: this won't be needed when string will be a type alias
    segments : base_list[string].new();
    for (segment : segments_list) {
      segments.append(base_string.from_list(segment));
    }
    return make_from_segments(segments.frozen_copy);
  }

  public overload static simple_name make(string name) {
    return make(utilities.s(name));
  }
}

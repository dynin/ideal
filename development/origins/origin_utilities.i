-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Utilities for manipulating |origin|s.
namespace origin_utilities {

  no_origin : special_origin.new("no-origin");
  builtin_origin : special_origin.new("[builtin]");

  source_content or null get_source(var origin or null the_origin) {
    while (the_origin is_not null) {
      if (the_origin is source_content) {
        return the_origin;
      }
      the_origin = the_origin.deeper_origin;
    }

    return missing.instance;
  }

  string get_source_prefix(var origin or null the_origin) {
    while (the_origin is_not null) {
      if (the_origin is text_origin) {
        source : the_origin.source;
        line_number : source.line_number(the_origin);
        return source.name ++ ":" ++ line_number ++ ": ";
      }
      if (the_origin is source_content) {
        return the_origin.name ++ ": ";
      }
      the_origin = the_origin.deeper_origin;
    }

    return "";
  }
}

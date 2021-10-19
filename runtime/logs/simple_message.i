-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A message that consists of a |log_level| and a string payload.
class simple_message {
  implements log_message;

  private log_level the_level;
  private string the_string;

  simple_message(log_level the_level, string the_string) {
    this.the_level = the_level;
    this.the_string = the_string;
  }

  implement log_level level() {
    return the_level;
  }

  implement string to_string() {
    return the_string;
  }

  implement text_fragment to_text() {
    if (the_string.is_empty) {
      -- This is kind of a hack, to make the browser render a newline.
      -- If the payload is non-empty but contains whitespace only, this breaks.
      return base_element.new(text_library.BR);
    } else {
      -- TODO: get rid of the cast.
      return base_element.new(text_library.DIV, the_string);
    }
  }
}

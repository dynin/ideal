-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Encapsulating string content that is used as a source file.
class source_content {
  extends debuggable;
  implements deeply_immutable data, mutable origin, stringable;

  identifier name;
  string content;

  overload source_content(resource_identifier module_id) {
    this.name = module_id;
    this.content = module_id.access_string(missing.instance).content;
  }

  overload source_content(identifier name, string content) {
    this.name = name;
    this.content = content;
  }

  implement origin or null deeper_origin => missing.instance;

  origin make_origin(nonnegative begin, nonnegative end) pure {
    return text_origin.new(this, begin, end);
  }

  --- Get a line number associated with given |text_origin|, starting from 1.
  -- TODO: the type should be a natural number (0 excluded);
  -- TODO: this can be optimized...
  nonnegative line_number(text_origin the_text_origin) {
    assert the_text_origin.source == this;
    assert the_text_origin.begin <= content.size;
    -- TODO: patterns
    var nonnegative count : 0;
    for (var nonnegative i : 0; i < the_text_origin.begin; i += 1) {
      if (content[i] == '\n') {
        count += 1;
      }
    }
    -- Note: we count lines starting from 1
    return count + 1;
  }

  implement string to_string => utilities.describe(this, name);
}

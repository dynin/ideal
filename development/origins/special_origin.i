-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Special origin label, such as source for referencing builtin types.
class special_origin {
  extends debuggable;
  implements mutable origin, deeply_immutable data, stringable;

  string description;

  special_origin(string description) {
    this.description = description;
  }

  implement origin or null deeper_origin => missing.instance;

  implement string to_string => description;
}

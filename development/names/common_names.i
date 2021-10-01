-- Copyright 2014-2021 The Ideal Authors. All rights reserved.

-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace common_names {

  instance_name : simple_name.make("instance");

  ideal_name : simple_name.make("ideal");
  library_name : simple_name.make("library");
  elements_name : simple_name.make("elements");
  operators_name : simple_name.make("operators");

  procedure_name : simple_name.make("procedure");
  function_name : simple_name.make("function");

  to_string_name : simple_name.make("to_string");

  size_name : simple_name.make("size");
  get_name : simple_name.make("get");

  private first : simple_name.make("first");
  private second : simple_name.make("second");
  private third : simple_name.make("third");

  simple_name make_numbered_name(nonnegative index) pure {
    if (index == 0) {
      return first;
    } else if (index == 1) {
      return second;
    } else if (index == 2) {
      return third;
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
}

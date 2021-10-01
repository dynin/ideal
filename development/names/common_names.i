-- Copyright 2014-2021 The Ideal Authors. All rights reserved.

-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace common_names {

  public INSTANCE_NAME : simple_name.make("instance");

  private FIRST : simple_name.make("first");
  private SECOND : simple_name.make("second");
  private THIRD : simple_name.make("third");

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
}

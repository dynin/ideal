-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace enum_util {
  boolean can_be_enum_value(construct the_construct) {
    return the_construct is name_construct || the_construct is parameter_construct;
  }
}

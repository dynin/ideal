-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An object that knows how to display itself.
-- TODO: this should be moved to a different namespace or retired.
interface displayable {
  implements readonly value;

  string display() pure;
}

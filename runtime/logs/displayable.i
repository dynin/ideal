-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- An object that knows how to display itself.
-- TODO: this should be moved to a different namespace or retired.
interface displayable {
  implements value;

  -- TODO: return text_fragment instead.
  string display;
}

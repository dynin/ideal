-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A text fragment can be a balanced |text_fragment|, or standalone
--- |start_element| and |end_element|.
interface text_event {
  extends deeply_immutable data, stringable;
}

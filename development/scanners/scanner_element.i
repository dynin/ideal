-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

interface scanner_element {
  extends data;

  set_config(the scanner_config);
  scan_state or null process(source_content source, nonnegative begin);
}

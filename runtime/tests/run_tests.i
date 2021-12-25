-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Driver program for running runtime unittests.
program run_tests {
  start() {
    all_tests.run_all_tests();
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

enum notification_level {
  implements deeply_immutable data, reference_equality;

  ERROR: new(log_level.ERROR);
  WARNING: new(log_level.WARNING);
  INFORMATIONAL: new(log_level.INFORMATIONAL);

  the log_level;

  private notification_level(the log_level) {
    this.the_log_level = the_log_level;
  }
}

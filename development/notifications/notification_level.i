-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

enum notification_level {
  ERROR: new(log_level.ERROR);
  WARNING: new(log_level.WARNING);
  INFORMATIONAL: new(log_level.INFORMATIONAL);

  the log_level;

  private notification_level(the log_level) {
    this.the_log_level = the_log_level;
  }
}

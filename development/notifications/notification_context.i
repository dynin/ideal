-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace notification_context {
  private var output[notification] or null logger;

  output[notification] get() {
    assert logger is_not null;
    return logger;
  }

  void set(output[notification] new_logger) {
    logger = new_logger;
  }
}

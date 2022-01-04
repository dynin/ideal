-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

namespace notification_context {
  private var output[notification] or null logger;

  output[notification] get() {
    assert logger is_not null;
    return logger;
  }

  set(output[notification] new_logger) {
    logger = new_logger;
  }
}

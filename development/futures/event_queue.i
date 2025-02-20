-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Encapsulating the list of events scheduled to be processed.
namespace event_queue {
  list[base_operation] pending : base_list[base_operation].new();

  boolean is_empty => pending.is_empty;

  schedule(base_operation the_operation) {
    pending.append(the_operation);
  }

  process() {
    -- TODO: drop explicit call
    while (!is_empty()) {
      next : pending.remove_at(0);
      next.execute();
    }
  }
}

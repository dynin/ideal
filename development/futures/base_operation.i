-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A concrete implementation of an |operation|.
class base_operation {
  implements operation;

  private procedure[void] callback;
  private string name;

  base_operation(procedure[void] callback, string name) {
    this.callback = callback;
    this.name = name;
  }

  implement schedule() {
    event_queue.schedule(this);
  }

  execute() {
    callback();
  }

  implement string to_string => name;
}

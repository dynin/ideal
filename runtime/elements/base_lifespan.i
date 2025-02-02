-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- An implementation of the core functionality of a |lifespan|.
class base_lifespan {
  implements lifespan;

  private registered : hash_set[closeable].new();

  base_lifespan(lifespan parent) {
    parent.register(this);
  }

  implement void register(the closeable) {
    registered.add(the_closeable);
  }

  implement void close() {
    -- TODO: handle "the closeable" here
    for (the_closeable : registered.elements) {
      the_closeable.close();
    }
    registered.clear();
  }

  implement void teardown() {
    -- TODO: handle "the closeable" here
    for (the_closeable : registered.elements) {
      the_closeable.teardown();
    }
    registered.clear();
  }

  implement lifespan make_sub_lifespan() {
    base_lifespan.new(this);
  }
}

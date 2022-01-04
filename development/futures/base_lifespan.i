-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- An implementation of a hierarchical lifespan.
class base_lifespan {
  implements lifespan;
  extends resource_manager;

  base_lifespan(lifespan or null parent) {
    if (parent is_not null) {
      -- TODO: cast is redundant
      parent.add_resource(this !> disposable);
    }
  }

  override lifespan make_sub_span() => base_lifespan.new(this);
}

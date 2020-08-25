-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An implementation of a hierarchical lifespan.
class base_lifespan {
  implements lifespan;
  extends resource_manager;

  base_lifespan(lifespan or null parent) {
    if (parent is_not null) {
      -- TODO: cast is redundant
      parent.add_resource(this as disposable);
    }
  }

  override lifespan make_sub_span() => base_lifespan.new(this);
}

-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A Lifespan is an object that manages a collection of disposable resources.
--- When a lifespan is disposed, all its children are disposed as well.
--- <p>
--- Lifespans are hierarchical; all lifespans have at most one parent lifespan,
--- and there are no cycles in the lifespan graph.
--- Lifespan hierarchy can correspond to the UI widget hierarchy,
--- data structure hierarchy and so on.
--- </p>
interface lifespan {
  implements disposable;

  -- Parent of this lifespan.
  -- Lifespan get parent;

  -- Zone that this lifespan belongs to.
  -- Zone get zone;

  --- Add a resource to this lifespan's resource collection.
  add_resource(disposable resource);

  --- Create a sublifespan with this lifespan as a parent.
  lifespan make_sub_span();
}

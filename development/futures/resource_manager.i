-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Encapsulates handling of resource management.
abstract class resource_manager {
  extends lifespan;

  set[disposable] resources : hash_set[disposable].new();

  override add_resource(disposable resource) {
    resources.add(resource);
  }

  override dispose() {
    for (resource : resources.elements) {
      resource.dispose();
    }
    resources.clear();
  }
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

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

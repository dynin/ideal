-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- Filesystem access endpoints.

implicit import ideal.library.elements;
implicit import ideal.library.resources;
import ideal.library.resources.resource_catalog;

namespace resources {
  namespace filesystem {
    resource_catalog CURRENT_CATALOG;
    resource_catalog ROOT;
  }

  namespace network {
    resource_identifier url(string url_string);
  }
}

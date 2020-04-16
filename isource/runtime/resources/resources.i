-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace resources {
  implicit import ideal.library.elements;
  implicit import ideal.library.resources;
  implicit import ideal.runtime.elements;

  enum base_extension;
  interface resource_store;
  class base_resource_catalog;
  class base_resource_store;
  class base_resource_identifier;
  class make_catalog_option;
  namespace resource_util;

  class test_resolver;
}

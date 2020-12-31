-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An option specifing that when a resource (file) is created,
--- all necessary parent catalogs are created as well.
--- (Similar to -p option to mkdir.)
-- TODO: use singleton.
class make_catalog_option {
  extends debuggable;
  implements access_option;

  -- TODO: infer construced type.
  public static final make_catalog_option instance : make_catalog_option.new();

  private make_catalog_option() { }
}

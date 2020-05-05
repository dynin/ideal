-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Principal type is an unflavored type.
interface principal_type {
  extends type;

  -- TODO: make this a variable declaration
  override action_name short_name() pure;
  kind get_kind;
  principal_type or null get_parent;
  declaration or null get_declaration;
  boolean has_flavor_profile;
  flavor_profile get_flavor_profile;
}

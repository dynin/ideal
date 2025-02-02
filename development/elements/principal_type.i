-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Principal type is an unflavored type.
interface principal_type {
  extends type;

  override action_name short_name;
  kind get_kind;
  principal_type or null get_parent;
  declaration or null get_declaration;
  boolean has_flavor_profile;
  flavor_profile get_flavor_profile;
}

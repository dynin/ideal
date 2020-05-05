-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Type flavor is a property of a type that specifies wehther it's mutable, readonly, etc.
interface type_flavor {
  extends modifier_kind, identifier, reference_equality;

  simple_name name;
  flavor_profile get_profile;
  immutable list[type_flavor] get_superflavors;
}

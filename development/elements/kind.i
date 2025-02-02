-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A description of the type family, such as |class|, |interface| or |datatype|.
interface kind {
  extends deeply_immutable data, reference_equality, stringable;

  simple_name name;
  flavor_profile default_profile;
  boolean is_namespace;
  boolean supports_constructors;
}

-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.runtime.elements.*;

public interface type_flavor extends modifier_kind, identifier, reference_equality {
  simple_name name();
  flavor_profile get_profile();
  immutable_list<type_flavor> get_superflavors();
}

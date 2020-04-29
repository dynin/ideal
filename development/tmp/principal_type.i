-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;

public interface principal_type extends type {
  action_name short_name();
  kind get_kind();
  @Nullable principal_type get_parent();
  @Nullable declaration get_declaration();
  boolean has_flavor_profile();
  flavor_profile get_flavor_profile();
}

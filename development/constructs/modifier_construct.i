-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

construct_data class modifier_construct {
  implements annotation_construct;

  modifier_kind the_kind;

  override string to_string => utilities.describe(this, the_kind);
}

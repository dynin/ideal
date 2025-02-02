-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

meta_construct class type_announcement_construct {
  readonly list[annotation_construct] annotations;
  kind kind;
  action_name name;

  override string to_string => utilities.describe(this, name);
}

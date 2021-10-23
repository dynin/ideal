-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

meta_construct class variable_construct {
  readonly list[annotation_construct] annotations;
  construct or null variable_type;
  action_name or null name;
  readonly list[annotation_construct] post_annotations;
  construct or null init;

  override string to_string => utilities.describe(this, name);
}

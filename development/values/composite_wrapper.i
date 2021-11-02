-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

interface composite_wrapper[any composite_value value_type] {
  extends value_wrapper[value_type], variable_context, stringable;
}

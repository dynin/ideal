-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

abstract class jump_wrapper {
  implements entity_wrapper;

  override type type_bound => common_types.unreachable_type;
}

-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- The parent type of identifiers used in ideal.
--- Usually it's a |simple_name|.
interface action_name {
  extends identifier, reference_equality;
}

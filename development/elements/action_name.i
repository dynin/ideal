-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- The parent type of identifiers used in ideal.
--- Usually it's a |simple_name|.
interface action_name {
  extends identifier, reference_equality;
}

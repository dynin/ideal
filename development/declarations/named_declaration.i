-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A declaration that has a name associated with it.
interface named_declaration {
  extends declaration;

  action_name short_name;
}

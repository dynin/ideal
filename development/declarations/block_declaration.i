-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declaration of a block, such as a static block.
interface block_declaration {
  extends declaration;

  annotation_set annotations;
  action get_body_action;
}

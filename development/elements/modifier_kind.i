-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A kind of modifier, such as |public| or |final|.
interface modifier_kind {
  extends identifier, reference_equality;

  simple_name name;
  -- TODO: boolean is_parametrizable();
}

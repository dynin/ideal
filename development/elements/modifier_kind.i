-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A kind of modifier, such as |public| or |final|.
interface modifier_kind {
  extends identifier, reference_equality;

  simple_name name;
  -- TODO: boolean is_parametrizable();
}

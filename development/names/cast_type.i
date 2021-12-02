-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A cast operator is either a soft or hard type.
class cast_type {
  extends operator;

  -- TODO: precedence other that relational
  protected cast_type(token_type name, string alpha_name) {
    super(operator_type.INFIX, name, alpha_name, precedence.RELATIONAL);
  }
}

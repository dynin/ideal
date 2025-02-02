-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A cast operator is either a soft or hard type.
class cast_type {
  extends operator;

  -- TODO: precedence other that relational
  protected cast_type(token_type name, string alpha_name) {
    super(operator_type.INFIX, name, alpha_name, precedence.RELATIONAL);
  }
}

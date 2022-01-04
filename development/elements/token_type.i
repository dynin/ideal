-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Token type is an unique identifier of a token class.
--- For exmaple, |PLUS| or |MINUS| identify punctuation tokens,
--- and |SIMPLE_NAME| identifies names.  For punctuation |token|s,
--- there is no payload needed, where as name tokens carry a payload.
interface token_type {
  extends identifier, reference_equality;

  --- Human-readable name.
  string name;

  --- Identifier used in grammar declarations.
  string symbol_identifier;
}
